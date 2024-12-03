package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NokonokoSprite;
import com.watabou.utils.Random;

public class Nokonoko extends Mob { // 엉금엉금: 죽으면 등껍집 드롭하는 몬스터, 방어력이 높은 편.

    {
        spriteClass = NokonokoSprite.class; //이미지 바꾸기
        HP = HT = 10;
        defenseSkill = 0;

        EXP = 2;
        maxLvl = 8;

        loot = Gold.class; // 등껍질 아이템 확정 드롭하게 할 예정, 일회용 원거리 무기?
        lootChance = 1f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 5 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 8;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(1, 4);
    }
}
