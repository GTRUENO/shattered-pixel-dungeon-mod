package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotLasherSprite;
import com.watabou.utils.Random;

public class PiranhaPlant extends Mob { // 뻐끔플라워: 움직이지 못한다, 원거리 공격을 한다, 체력, 회피가 낮다.

    {
        spriteClass = RotLasherSprite.class; // 이미지 바꾸기

        HP = HT = 4;
        defenseSkill = 0;

        EXP = 2;
        maxLvl = 8;

        loot = Generator.Category.SEED; // 무작위 씨앗 드랍
        lootChance = 0.1f;

        properties.add(Property.IMMOVABLE); // 움직이지 못함
    }


    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int attackSkill(Char target) {
        return 4;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 1);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return this.fieldOfView[enemy.pos] && Dungeon.level.distance(this.pos, enemy.pos) <= 6 // 사거리 6까지 원거리 공격 가능
                && new Ballistica( pos, enemy.pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == enemy.pos
                && new Ballistica( enemy.pos, pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID).collisionPos == pos;
    } // 장애물에 막히지 않아야 공격 가능
}