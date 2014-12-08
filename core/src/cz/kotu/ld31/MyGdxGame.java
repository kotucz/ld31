package cz.kotu.ld31;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MyGdxGame extends ApplicationAdapter {

    public static final boolean DEBUG = false;

    final Color DARK_GREEN = new Color(0, 0.5f, 0, 1);

    Res res;

    Levels levels;

    SpriteBatch batch;
    ExtendViewport viewport;
    Stage stage;

    float unitPx = 20;
    Grid grid;
    Grid.Stone handStone;
    Actor winAnimation;
    TextButton[] levelButtons;
    boolean victoryShown;
    int currentLevel;
    int currentTargets;
    Group boardGroup;

    @Override
    public void create() {
        Res.init();
        res = Res.getInstance();
        levels = new Levels();


        batch = new SpriteBatch();

        viewport = new ExtendViewport(320, 240);
        stage = new Stage(viewport);

        if (DEBUG) {
//            stage.setDebugAll(true);
//            currentLevel = 7;
        }

        Gdx.input.setInputProcessor(new InputMultiplexer(
        stage,
        new MoveInputProcessor()));

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

        currentTargets = 0;
        victoryShown = false;

        boardGroup = new Group();
        stage.addActor(boardGroup);

        if (currentLevel < levels.LIST.length) {
            loadLevel(boardGroup, levels.LIST[currentLevel]);
        } else {
            // show thanx -> targetCount==0
        }

        winAnimation = new Image(res.win);
        winAnimation.setBounds(0, 0, 240, 240);
        winAnimation.setOrigin(Align.center);
        resetVictoryAnimation();
        stage.addActor(winAnimation);

        setupUi();
    }

    void startNextLevel() {
        currentLevel++;
        resetLevel();
    }

    void startLevel(int level) {
        currentLevel = level;
        resetLevel();
    }

    public void loadLevel(Group group, Level level) {

        grid = new Grid(level.width, level.height);
        group.addActor(grid);

        int i = 0;
        for (int y = level.height - 1; y >= 0; y--) {
            for (int x = 0; x < level.width; x++) {
                char c = level.fields.charAt(i);
                switch (c) {
                    case ' ':
                        break;
                    case '0':
                        Grid.Stone stone = grid.putStone(new Vec(x, y));
                        group.addActor(stone);
                        // just select one
                        handStone = stone;
                        break;
                    case 'X':
                        grid.getField(x, y).type = Type.SOLID;
                        break;
                    case 'T':
                        grid.getField(x, y).type = Type.TARGET;
                        currentTargets++;
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

    private void setupUi() {
        Table table = new Table();
        table.setFillParent(true);
        Skin skin = new Skin();
        BitmapFont bitmapFont = new BitmapFont();
        skin.add("default", bitmapFont);
        Label.LabelStyle labelStyle = new Label.LabelStyle(bitmapFont, Color.WHITE);
        skin.add("default", labelStyle);
        table.setSkin(skin);

        Container thanksLabel = new Container<>(new Label("Thanks for playing!", labelStyle)).pad(42);
        // no level has 0 targets
        thanksLabel.setVisible(currentTargets == 0);
        table.add(thanksLabel).align(Align.top).width(240);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = bitmapFont;
        buttonStyle.up = new TextureRegionDrawable(res.stone);

        {
            final int perRow = 2;
            Table menuTable = new Table();
            menuTable.setSkin(skin);

            int rb = 0; // num buttons on row
            levelButtons = new TextButton[levels.LIST.length];
            for (int lvl = 0; lvl < levels.LIST.length; lvl++) {
                addLevelButton(menuTable, buttonStyle, lvl);
                rb++;
                if (rb == perRow) {
                    menuTable.row();
                    rb = 0;
                }
            }
            if (rb != 0) {
                menuTable.row();
            }
            menuTable.add("#LD31").colspan(perRow);
            menuTable.row();
            menuTable.add("@kotucz").colspan(perRow);
            table.add(menuTable).expand();
        }


        stage.addActor(table);
    }

    private void addLevelButton(Table menuTable, TextButton.TextButtonStyle buttonStyle, final int level) {
        TextButton button = new TextButton("" + level, buttonStyle);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("button pressed", "level " + level);
                startLevel(level);
            }
        });
        menuTable.add(button);
        levelButtons[level] = button;
    }

    @Override
    public void render() {

        // process all input

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

        // update visuals

        updateGridPos();

        highlightStones();

        highlightLevels();

        // perform drawing

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // nice background image
        batch.begin();
        res.backgroundTiled.draw(batch, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);

        stage.draw();
    }

    private void updateGridPos() {

//        float availableWidth = viewport.getWorldWidth();
        float availableWidth = 240;
        float availableHeight = viewport.getWorldHeight();
//        float scale = Math.min(availableWidth / grid.width, viewport.getWorldHeight() / grid.height);
        float scale = availableWidth / Math.max(grid.width, 8);
        boardGroup.setScale(scale);
        float newWidth = grid.width * scale;
        float newHeight = grid.height * scale;
        boardGroup.setPosition((availableWidth - newWidth) / 2, (availableHeight - newHeight) / 2);

        unitPx = scale;
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

    private void highlightLevels() {
        for (int i = 0; i < levelButtons.length; i++) {
            Color color;
            if (currentLevel == i) {
                color = Color.WHITE;
            } else if (i < currentLevel) {
                color = DARK_GREEN;
            } else {
                color = Color.GRAY;
            }
            levelButtons[i].setColor(color);
        }
    }

    private void highlightStones() {
        if (isAnimatingMove()) {
            return;
        }
        // highlight selected stone
        for (Grid.Stone stone : grid.stones) {
            Color color;
            if (stone.isOnTarget()) {
                if (handStone == stone) {
                    color = Color.GREEN;
                } else {
                    color = DARK_GREEN;
                }
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
        for (Actor actor : boardGroup.getChildren()) {
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
        return (metTargets == currentTargets);
    }

    private void resetVictoryAnimation() {
        winAnimation.setColor(1, 1, 1, 0);
        winAnimation.setScale(0.25f);
    }

    private void animateVictory() {
        float durationAlpha = 1.5f; // seconds
        float durationScale = 2; // seconds
        float finalScale = 0.5f; // percent of screen
        Interpolation interpolation = Interpolation.bounceOut;
        winAnimation.addAction(
        Actions.parallel(
        Actions.alpha(1, durationAlpha),
        Actions.scaleTo(finalScale, finalScale, durationScale, interpolation)
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

            if (unitPx * unitPx < dragDir.len2()) {
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
