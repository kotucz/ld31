package cz.kotu.ld31;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MyGdxGame extends ApplicationAdapter {

    Res res;

    SpriteBatch batch;
    ExtendViewport viewport;
    Stage stage;

    Grid grid;
    Grid.Stone handStone;

    @Override
    public void create() {
        Res.init();
        res = Res.getInstance();

        batch = new SpriteBatch();

        viewport = new ExtendViewport(20, 15);
        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(new MoveInputProcessor());

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

        Level level = new Level(7, 7,
        "   X   " +
        " 0   0 " +
        "       " +
        "X  T  X" +
        "       " +
        " 0   0 " +
        "   X   "
        );

        loadLevel(level);
    }

    public void loadLevel(Level level) {
        grid = new Grid(level.width, level.height);
        stage.addActor(grid);

        int i = 0;
        for (int y = level.height - 1; y >= 0; y--) {
            for (int x = 0; x < level.width; x++) {
                char c = level.fields.charAt(i);
                switch (c) {
                    case ' ':
                        break;
                    case '0':
                        Grid.Stone stone = grid.putStone(new Vec(x, y));
                        stage.addActor(stone);
                        // just select one
                        handStone = stone;
                        break;
                    case 'X':
                        grid.getField(x, y).type = Type.SOLID;
                        break;
                    case 'T':
                        grid.getField(x, y).type = Type.TARGET;
                        break;
                    case 'H':
                        grid.getField(x, y).type = Type.HOLE;
                        break;
                    default:
                        Gdx.app.log("loadLevel", String.format("wrong token (%d,%d) [%s]", x, y, c));
                }
                i++;
            }
        }
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
        Dir dir = null;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            dir = Dir.N;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            dir = Dir.S;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            dir = Dir.E;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            dir = Dir.W;
        }
        doMoveIfPossible(dir);
    }

    private void doMoveIfPossible(Dir dir) {
        if (handStone != null && dir != null && dir != Dir.O) {
            Vec where = new Vec();
            if (handStone.canMove(dir.vec(), where)) {
                handStone.doMoveTo(where);
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

    class MoveInputProcessor extends InputAdapter {

        final Vector3 touchPos = new Vector3();
        final Vector3 startDragPos = new Vector3();
        final Vector3 dragDir = new Vector3();
        Dir moveDir = Dir.O;

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            unproject(screenX, screenY);

            startDragPos.set(touchPos);

            Actor touchedActor = stage.hit(touchPos.x, touchPos.y, true);
            if (touchedActor instanceof Grid.Stone) {
                handStone = ((Grid.Stone) touchedActor);
                Gdx.app.log("touchDown", String.format("grabbed %s", handStone));
                return true;
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {

            unproject(screenX, screenY);

            // vector from start drag to current pos
            dragDir.set(touchPos);
            dragDir.sub(startDragPos);

            if (1 * 1 < dragDir.len2()) {
                if (Math.abs(dragDir.x) < Math.abs(dragDir.y)) {
                    moveDir = dragDir.y < 0 ? Dir.S : Dir.N;
                } else {
                    moveDir = dragDir.x < 0 ? Dir.W : Dir.E;
                }
            } else {
                moveDir = Dir.O;
            }

            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {

            unproject(screenX, screenY);

            doMoveIfPossible(moveDir);
//            return stage.touchUp(touchPos.x, touchPos.y, pointer, button);
            return false;
        }

        private void unproject(int screenX, int screenY) {
            touchPos.set(screenX, screenY, 0);
            viewport.getCamera().unproject(touchPos);
        }

    }

}
