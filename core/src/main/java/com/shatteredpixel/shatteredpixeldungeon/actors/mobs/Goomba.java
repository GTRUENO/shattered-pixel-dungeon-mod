package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GoombaSprite;
import com.watabou.utils.Random;

public class Goomba extends Mob { // 굼바: 특징없는 일반 몬스터

    {
        spriteClass = GoombaSprite.class;

        HP = HT = 8;
        defenseSkill = 0; // 회피 확률

        maxLvl = 5; // 최대 레벨
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 4 );
    } // 공격 데미지

    @Override
    public int attackSkill( Char target ) {
        return 8;
    } // 공격 적중률

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 1);
    } //데미지 경감 수치
}