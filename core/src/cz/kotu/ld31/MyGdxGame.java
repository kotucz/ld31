package cz.kotu.ld31;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MyGdxGame extends ApplicationAdapter {

    Res res;

    SpriteBatch batch;
    ExtendViewport viewport;
    Stage stage;

    Grid grid;
    Grid.Stone stone0;

    @Override
    public void create() {
        Res.init();
        res = Res.getInstance();

        batch = new SpriteBatch();

        viewport = new ExtendViewport(20, 15);
        stage = new Stage(viewport);

        resetLevel();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    void resetLevel() {
        stage.getActors().clear();

        viewport.setMinWorldWidth(9);
        viewport.setMinWorldHeight(9);

        grid = new Grid();
        stage.addActor(grid);

        stone0 = grid.putStone(new Vec(6, 6));
        stage.addActor(stone0);

        stone0 = grid.putStone(new Vec(2, 5));
        stage.addActor(stone0);

        stone0 = grid.putStone(new Vec(2, 1));
        stage.addActor(stone0);
    }

    @Override
    public void render() {
        processInput();

        stage.act(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(viewport.getCamera().combined);

        stage.draw();

    }

    void processInput() {
        if (isAnimatingMove()) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetLevel();
            return;
        }
        Vec dir = null;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            dir = new Vec(0, 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            dir = new Vec(0, -1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            dir = new Vec(1, 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            dir = new Vec(-1, 0);
        }
        if (dir != null) {
            Vec where = new Vec();
            if (stone0.canMove(dir, where)) {
                stone0.doMoveTo(where);
            }
        }
    }

    private boolean isAnimatingMove() {
        for (Actor actor : stage.getActors()) {
            if (actor.getActions().size > 0) {
                return true;
            }
        }
        return false;
    }

}
