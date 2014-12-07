package cz.kotu.ld31;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid extends Actor {

    private Res res;
    private final Random random = new Random();

    final int width;
    final int height;

    final List<Field> fields;
    private Field outsideField;

    final List<Grid.Stone> stones = new ArrayList<>();

    public Grid(int width, int height) {
        res = Res.getInstance();

        outsideField = new Field();
        outsideField.type = Type.BORDER;

        this.width = width;
        this.height = height;

        fields = new ArrayList<>(this.width * this.height);
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Field field = new Field();
                fields.add(field);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        int border = 2;
        for (int y = 0 - border; y < height + border; y++) {
            for (int x = 0 - border; x < width + border; x++) {
                Grid.Field field = getField(x, y);

                TextureRegion texture = res.getTextureForType(field.type);
                if (texture != null) {
                    batch.draw(texture, x, y, 1, 1);
                }

            }
        }
    }

    private void drawGrid(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color color = new Color();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Grid.Field field = getField(x, y);
                shapeRenderer.setColor(color.set(field.color));
                shapeRenderer.rect(x, y, 1, 1);
            }
        }
        shapeRenderer.end();
    }

    Vec randomPos() {
        return new Vec(random.nextInt(width), random.nextInt(height));
    }

    Field getField(Vec pos) {
        return getField(pos.x, pos.y);
    }

    Field getField(int x, int y) {
        if (0 <= x && x < this.width && 0 <= y && y < height) {
            return fields.get(x + y * width);
        } else {
            return outsideField;
        }
    }

    public Stone putStone(Vec pos) {
        Stone stone = new Stone();
        stone.setGridPos(pos);
        getField(pos).put(stone);
        stones.add(stone);
        return stone;
    }

    class Field {

        Type type = Type.VOID;
        int color = random.nextInt();
        Stone stone = null;

        boolean isFree() {
            if (stone != null) {
                return false;
            }
            switch (type) {
                case BORDER:
                case SOLID:
                case STONE:
                    return false;
                default:
                    return true;
            }
        }

        public void remove(Stone stone) {
            if (this.stone != stone) {
                Gdx.app.log("BUG", "removing another stone!");
                throw new IllegalArgumentException("removing another stone!");
            }
            this.stone = null;
        }

        public void put(Stone stone) {
            this.stone = stone;
        }
    }

    public class Stone extends Actor {
        // grid position
        final Vec pos = new Vec();
        final Type type = Type.STONE;

        public Stone() {
            setSize(1, 1);
        }

        void setGridPos(Vec pos) {
            setGridPos(pos.x, pos.y);
        }

        void setGridPos(int x, int y) {
            pos.set(x, y);
            setPosition(x, y);
        }

        protected Grid.Field relativeField(int dx, int dy) {
            return getField(pos.x + dx, pos.y + dy);
        }

        protected Grid.Field relativeField(Vec dir) {
            return getField(Vec.add(pos, dir));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

            TextureRegion texture = res.getTextureForType(type);
            batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        }

        /**
         * @param dir   unity direction
         * @param where result move position
         * @return true if can be moved
         */
        boolean canMove(Vec dir, Vec where) {
            where.set(pos);
            for (int d = 0; d < 1000; d++) {
                where.set(pos);
                // look one step forward
                where.addMultiple(dir, d + 1);
                Field nextField = getField(where);
                if (!nextField.isFree()) {
                    where.set(pos);
                    where.addMultiple(dir, d);
                    return 0 < d;
                }
            }
            return false;
        }

        boolean doMoveTo(Vec to) {
            final int dist = pos.dist1(to);

            final Field currentField = getField(pos);
            currentField.remove(this);

            // do move
            pos.set(to);

            final Field newField = getField(pos);
            newField.put(this);

            // animate motion
            // longer distances shall not take linearly more time - because of acceleration
            float moveDuration = (float) Math.pow(dist, 0.5f) * 0.15f; // seconds
            addAction(Actions.moveTo(pos.x, pos.y, moveDuration, Interpolation.fade));
            return true;
        }

        @Override
        public String toString() {
            return "Stone{" +
            "pos=" + pos +
            '}';
        }
    }
}