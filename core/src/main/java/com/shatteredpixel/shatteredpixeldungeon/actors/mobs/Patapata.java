package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PatapataSprite;
import com.watabou.utils.Random;


public class Patapata extends Mob { // 펄럭펄럭: 비행형, 사망 시 엉금엉금을 스폰함
    {
        spriteClass = PatapataSprite.class; // 이미지 바꾸기

        HP = HT = 5;
        defenseSkill = 0;

        EXP = 1;
        maxLvl = 8;

        flying = true;

    }

    @Override
    public void die(Object cause) { // 죽으면 엉금엉금 생성
        flying = false;
        super.die(cause);
        Mob newNoko = new Nokonoko();
        newNoko.state = newNoko.HUNTING;
        newNoko.pos = this.pos;
        GameScene.add(newNoko);
        Dungeon.level.mobs.add(newNoko);
        Dungeon.level.occupyCell(newNoko); // 위치 점유

        // 펄럭펄럭이 죽을 때 가지고 있던 버프, 디버프를 유지
        if (buff( Burning.class ) != null) {
            Buff.affect( newNoko, Burning.class ).reignite( newNoko );
        }
        if (buff( Poison.class ) != null) {
            Buff.affect( newNoko, Poison.class ).set(2);
        }
        for (Buff b : buffs()) {
            if (b.revivePersists) {
                Buff.affect(newNoko, b.getClass());
            }
        }
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
