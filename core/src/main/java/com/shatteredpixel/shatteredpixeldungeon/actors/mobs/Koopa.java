package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KoopaSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class Koopa extends Mob {

    {
        HP = HT = 100;
        EXP = 10;
        defenseSkill = 8;
        spriteClass = KoopaSprite.class;

        properties.add(Property.BOSS);
        properties.add(Property.FIERY);
    }

    private int breath = 0;

    @Override
    public int damageRoll() {
        int min = 1;
        int max = (HP*2 <= HT) ? 2 : 1; // hp 절반 이하면 스텟 강화
        if (breath > 0) {
            if (enemy == Dungeon.hero) {
                Statistics.qualifiedForBossChallengeBadge = false;
                Statistics.bossScores[0] -= 100;
            }
            return Random.NormalIntRange( min*3, max*3 ); // 특수 스킬은 데미지 3배
        } else {
            return Random.NormalIntRange( min, max );
        }
    }

    @Override
    public int attackSkill( Char target ) {
        int attack = 10;
        if (HP*2 <= HT) attack = 15;
        if (breath > 0) attack *= 2; // 특수 스킬은 명중률 2배
        return attack;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return (int)(super.defenseSkill(enemy) * ((HP*2 <= HT)? 1.5 : 1));
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(2, 5);
    }

    @Override
    public boolean act() {

        if (state != HUNTING && breath > 0){ // 사냥 상태가 아니면 특수 공격 취소, 대기 상태
            breath = 0;
            sprite.idle();
        }


        if (state != SLEEPING){
            Dungeon.level.seal(); // 보스를 처치 해야지 다른 층으로 갈 수 있음
        }

        return super.act();
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        if (breath > 0){ // 특수 공격시 공격 범위 확장
            //we check both from and to in this case as projectile logic isn't always symmetrical.
            //this helps trim out BS edge-cases
            return Dungeon.level.distance(enemy.pos, pos) <= 2
                    && new Ballistica( pos, enemy.pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == enemy.pos
                    && new Ballistica( enemy.pos, pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == pos;
                    // 장애물에 막히지 않아야 공격가능
        } else {
            return super.canAttack(enemy);
        }
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (breath > 0) {
            PixelScene.shake( 3, 0.2f ); // 특수 공격시 화면 흔들림 효과
            Buff.affect(enemy, Burning.class).reignite(enemy); // 특수 공격시 화상 부여
            if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
            breath = 0;
        }

        return damage;
    }

    @Override
    public void updateSpriteState() {
        super.updateSpriteState();

        if (breath > 0){
            ((KoopaSprite)sprite).pumpUp( breath );
        }
    }

    @Override
    protected boolean doAttack( Char enemy ) {
        if (breath == 1) {
            breath++;
            ((KoopaSprite)sprite).pumpUp( breath );

            spend( attackDelay() );

            return true;
        } else if (breath >= 2 || Random.Int( (HP*2 <= HT) ? 2 : 5 ) > 0) {

            boolean visible = Dungeon.level.heroFOV[pos];

            if (visible) {
                if (breath >= 2) {
                    ((KoopaSprite) sprite).pumpAttack();
                } else {
                    sprite.attack(enemy.pos);
                }
            } else {
                if (breath >= 2){
                    ((KoopaSprite)sprite).triggerEmitters();
                }
                attack( enemy );
                Invisibility.dispel(this);
                spend( attackDelay() );
            }

            return !visible;

        } else {

            if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
                breath += 2;
                //don't want to overly punish players with slow move or attack speed
                spend(GameMath.gate(attackDelay(), (int)Math.ceil(enemy.cooldown()), 3*attackDelay()));
            } else {
                breath++;
                spend( attackDelay() );
            }

            ((KoopaSprite)sprite).pumpUp( breath );

            if (Dungeon.level.heroFOV[pos]) {
                sprite.showStatus( CharSprite.WARNING, Messages.get(this, "!!!") );
                GLog.n( Messages.get(this, "pumpup") );
            }

            return true;
        }
    }

    @Override
    public boolean attack( Char enemy, float dmgMulti, float dmgBonus, float accMulti ) {
        boolean result = super.attack( enemy, dmgMulti, dmgBonus, accMulti );
        if (breath > 0) {
            breath = 0;
            if (enemy == Dungeon.hero) {
                Statistics.qualifiedForBossChallengeBadge = false;
                Statistics.bossScores[0] -= 100;
            }
        }
        return result;
    }

    @Override
    protected boolean getCloser( int target ) {
        if (breath != 0) {
            breath = 0;
            sprite.idle();
        }
        return super.getCloser( target );
    }

    @Override
    protected boolean getFurther(int target) {
        if (breath != 0) {
            breath = 0;
            sprite.idle();
        }
        return super.getFurther( target );
    }

    @Override
    public void damage(int dmg, Object src) {
        if (!BossHealthBar.isAssigned()){
            BossHealthBar.assignBoss( this ); // 보스 체력바 설정
            Dungeon.level.seal(); // 던전 봉쇄
        }
        boolean bleeding = (HP*2 <= HT);
        super.damage(dmg, src);
        if ((HP*2 <= HT) && !bleeding){ // 체력 절반 이하 일때
            BossHealthBar.bleed(true); // 보스 체력바에 출혈 표시
            sprite.showStatus(CharSprite.WARNING, Messages.get(this, "enraged")); // 분노 상태 표시
            yell(Messages.get(this, "gluuurp")); // 보스 대사
        }
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
            if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(dmg);
            else                                                    lock.addTime(dmg*1.5f);
        }
    }

    @Override
    public void die( Object cause ) {

        super.die( cause );

        Dungeon.level.unseal(); // 던전 봉쇄 해제

        GameScene.bossSlain(); // 보스 처치 이벤트
        Dungeon.level.drop( new SkeletonKey( Dungeon.depth ), pos ).sprite.drop(); // 열쇠 드롭

        Badges.validateBossSlain(); // 업적 획득
        Statistics.bossScores[0] += 1000; // 점수 획득

        yell( Messages.get(this, "defeated") ); // 패배 대사
    }

    @Override
    public void notice() { // 보스 방 진입시
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this); // 보스 체력바 설정
            Dungeon.level.seal(); // 던전 봉쇄
            yell(Messages.get(this, "!!!")); // 발견 대사
        }
    }

    private final String BREATH = "breath";

    @Override
    public void storeInBundle( Bundle bundle ) { // 게임 상태 저장

        super.storeInBundle( bundle );

        bundle.put( BREATH , breath );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) { // 게임 상태 불러오기

        super.restoreFromBundle( bundle );

        breath = bundle.getInt( BREATH );
        if (state != SLEEPING) BossHealthBar.assignBoss(this);
        if ((HP*2 <= HT)) BossHealthBar.bleed(true);
    }

}
