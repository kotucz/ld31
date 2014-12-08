package cz.kotu.ld31.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import cz.kotu.ld31.MyGdxGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Sliding StOnes :: Tomas Kotula (kotucz) :: Ludum Dare 31";
        config.addIcon("icon32.png", Files.FileType.Internal);
        new LwjglApplication(new MyGdxGame(), config);
    }
}
