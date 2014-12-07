package cz.kotu.ld31;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MyGdxGame extends ApplicationAdapter {

    public static final boolean DEBUG = true;

    Res res;

    Levels levels;

    SpriteBatch batch;
    ExtendViewport viewport;
    Stage stage;

    Grid grid;
    Grid.Stone handStone;
    Image winAnimation;
    boolean victoryShown;
    int currentLevel;

    @Override
    public void create() {
        Res.init();
        res = Res.getInstance();
        levels = new Levels();

        batch = new SpriteBatch();

        viewport = new ExtendViewport(20, 15);
        stage = new Stage(viewport);

        if (DEBUG) {
            stage.setDebugAll(true);
            currentLevel = 2;
        }

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

        loadLevel(levels.LIST[currentLevel]);

        victoryShown = false;
        winAnimation = new Image(res.green);
        winAnimation.setColor(1, 1, 1, 0);
        winAnimation.setPosition(5, 5);
        winAnimation.setScale(1f / 32);
        winAnimation.setBounds(0, 0, 1, 1);
        stage.addActor(winAnimation);
    }

    void startNextLevel() {
        currentLevel++;
        resetLevel();
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
        if (!isAnimatingMove()) {
            if (!victoryShown) {
                if (isVictory()) {
                    // TODO mark level solved
                    animateVictory();
                    victoryShown = true;
                }
                processInput();
            } else {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                    startNextLevel();
                    return;
                }
            }
        }

        stage.act(Gdx.graphics.getDeltaTime());

        // TODO set nice background image
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(viewport.getCamera().combined);

        highlightStones();

        stage.draw();
    }

    void processInput() {
        if (isAnimatingMove()) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_RIGHT) ||
        Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT) ||
        Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            switchStone();
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

    private boolean doMoveIfPossible(Dir dir) {
        if (handStone != null && dir != null && dir != Dir.O) {
            Vec where = new Vec();
            if (handStone.canMove(dir.vec(), where)) {
                return handStone.doMoveTo(where);
            }
        }
        return false;
    }

    private void highlightStones() {
        if (isAnimatingMove()) {
            return;
        }
        // highlight selected stone
        for (Grid.Stone stone : grid.stones) {
            Color color;
            if (stone.isOnTarget()) {
                color = Color.GREEN;
            } else if (handStone == stone) {
                color = Color.WHITE;
            } else {
                color = Color.GRAY;
            }
            stone.setColor(color);
        }
    }

    private void switchStone() {
        int i = grid.stones.indexOf(handStone);
        i++;
        i %= grid.stones.size();
        handStone = grid.stones.get(i);
    }

    private boolean isAnimatingMove() {
        for (Actor actor : stage.getActors()) {
            if (actor.getActions().size > 0) {
                return true;
            }
        }
        return false;
    }

    boolean isVictory() {
        int metTargets = 0;
        for (Grid.Stone stone : grid.stones) {
            if (stone.isOnTarget()) {
                metTargets++;
            }
        }
        // TODO multiple targets
        return (metTargets == 1);
    }

    private void animateVictory() {
        float duration = 3; // seconds
        winAnimation.addAction(
        Actions.parallel(
        Actions.alpha(1, duration),
        Actions.scaleBy(2, 2, duration)
        ));
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
                handStone.setColor(0, 1, 0, 1);
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
            if (isAnimatingMove()) {
                return false;
            } else if (victoryShown) {
                startNextLevel();
                return true;
            }

            unproject(screenX, screenY);

            return doMoveIfPossible(moveDir);
        }

        private void unproject(int screenX, int screenY) {
            touchPos.set(screenX, screenY, 0);
            viewport.getCamera().unproject(touchPos);
        }

    }

}
