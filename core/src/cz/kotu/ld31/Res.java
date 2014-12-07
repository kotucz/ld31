package cz.kotu.ld31;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * @author tkotula
 */
public class Res {

    static Res instance;

    static void init() {
        instance = new Res();
    }

    TextureAtlas atlas = new TextureAtlas("art.pack");

    TextureRegion stone1 = atlas.findRegion("stone");
    TextureRegion ground1 = atlas.findRegion("ground");
    TextureRegion background = atlas.findRegion("background-large");
    TiledDrawable backgroundDrawable = new TiledDrawable(background);
    TextureRegion box = atlas.findRegion("box");
    TextureRegion target = atlas.findRegion("target");

    TextureRegion questionMark = atlas.findRegion("questionmark");

    TextureRegion win = atlas.findRegion("win");

    static Res getInstance() {
        return instance;
    }

    TextureRegion getTextureForType(Type type) {
        switch (type) {
            case VOID:
                return ground1;
            case STONE:
                return stone1;
            case BORDER:
            case SOLID:
                return box;
            case TARGET:
                return target;
            default:
                return questionMark;
        }
    }

}
