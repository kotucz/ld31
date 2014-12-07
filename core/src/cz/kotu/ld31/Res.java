package cz.kotu.ld31;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author tkotula
 */
public class Res {

    static Res instance;

    static void init() {
        instance = new Res();
    }

    Texture badlogic = new Texture("badlogic.jpg");
    Texture pixelArt = new Texture("art.png");

    TextureRegion stone1 = subregion(0, 0);
    TextureRegion ground1 = subregion(2, 1);
    TextureRegion solid1 = subregion(1, 7);
    TextureRegion target1 = subregion(1, 3);
    TextureRegion target2 = subregion(4, 4);

    TextureRegion questionMark = subregion(4, 2);

    TextureRegion green = subregion(0, 5);

    static Res getInstance() {
        return instance;
    }

    private TextureRegion subregion(int col, int row) {
        int S = 32;
        return new TextureRegion(pixelArt, col * S, row * S, S, S);
    }

    TextureRegion getTextureForType(Type type) {
        switch (type) {
            case VOID:
                return ground1;
            case STONE:
                return stone1;
            case BORDER:
            case SOLID:
                return solid1;
            case TARGET:
                return target1;
            default:
                return questionMark;
        }
    }

}
