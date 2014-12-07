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

    TextureRegion stone = atlas.findRegion("stone");
    TextureRegion ground = atlas.findRegion("ground2");
    TiledDrawable groundTiled = new TiledDrawable(ground);
    TextureRegion background = atlas.findRegion("background-large");
    TiledDrawable backgroundTiled = new TiledDrawable(background);
    TextureRegion box = atlas.findRegion("box2");
    TextureRegion target = atlas.findRegion("target");

    TextureRegion questionMark = atlas.findRegion("questionmark");

    TextureRegion win = atlas.findRegion("win");

    static Res getInstance() {
        return instance;
    }

    TextureRegion getTextureForType(Type type) {
        switch (type) {
            case VOID:
                return ground;
            case STONE:
                return stone;
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
