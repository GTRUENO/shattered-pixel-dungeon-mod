package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.watabou.utils.Random;

public class Lakitu extends Mob { // 김수한무: 비행형 적, 사거리2 원거리 공격, 부유의 물약 드랍

    {
        spriteClass = BatSprite.class; // 이미지 바꾸기

        HP = HT = 10;
        defenseSkill = 10;

        EXP = 3;
        maxLvl = 9;

        flying = true; // 비행

        loot = new PotionOfLevitation(); // 부유의 물약 드랍
        lootChance = 0.1f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 8 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 6;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return this.fieldOfView[enemy.pos] && Dungeon.level.distance(this.pos, enemy.pos) <= 2; // 사거리 2까지 원거리 공격 가능
    }

    @Override
    public void die(Object cause) {
        flying = false;
        super.die(cause);
    } // 사망시 비행 종료
}