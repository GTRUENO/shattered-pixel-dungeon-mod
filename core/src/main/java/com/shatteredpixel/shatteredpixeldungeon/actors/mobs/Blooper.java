package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlooperSprite;
import com.watabou.utils.Random;

public class Blooper extends Mob { // 징오징오: 비행형 적, 피격시 확률로 실명 부여

    {
        spriteClass = BlooperSprite.class; // 이미지 바꾸기

        HP = HT = 16;
        defenseSkill = 0;

        EXP = 4;
        maxLvl = 9;

        flying = true; // 비행
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 6 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 10;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }

    @Override
    public int defenseProc( Char enemy, int damage ) {
        if (Random.Int(5) == 0) { // 1/5확률로 부여
            Buff.prolong(enemy, Blindness.class, Blindness.DURATION/2f ); // 5턴간 실명 부여
            enemy.sprite.burst(0x000000, 5); // 검은색 폭발효과
        }
        return super.defenseProc( enemy, damage );
    }

    @Override
    public void die(Object cause) {
        flying = false;
        super.die(cause);
    } // 사망시 비행 종료
}