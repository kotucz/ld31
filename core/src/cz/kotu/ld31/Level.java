package cz.kotu.ld31;

import com.badlogic.gdx.Gdx;

public class Level {

    int width;
    int height;

    String fields;

    public Level(int width, int height, String fields) {
        this.width = width;
        this.height = height;
        this.fields = fields;
        if (fields.length() != width * height) {
            Gdx.app.log("Level", String.format("level dimensions dos not match %dx%d [%s]", width, height, fields));
        }
    }
}
