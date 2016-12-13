package com.heerbann.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.heerbann.box2dcamera.Box2DCamera;
import com.heerbann.box2dcamera.Box2DCamera.RenderItem;

public class Test extends ApplicationAdapter {

	private World world;
	private Box2DCamera test1, test2, test3, test4, test5, test6;
	private SpriteBatch batch;
	
	public final static float pixel2Box = 0.05f, box2Pixel = 20;
	
	@Override
	public void create () {
		world = new World(new Vector2(0, 0), true); 
		batch = new SpriteBatch();
		
		int w = (int)(Gdx.graphics.getWidth() / 3);
		int h = (int)(Gdx.graphics.getHeight() / 2);
		
		test1 = new Box2DCamera(world, batch, 5, 5, w-10, h-10, box2Pixel);
		//test1.setDebug(true);
		test1.getCamera().position.setZero();
		test1.setGlClearColor(Color.CHARTREUSE);
		
		test2 = new Box2DCamera(world, batch, w+5, 5, w-10, h-10, box2Pixel);
		test2.getCamera().position.setZero();
		test2.setGlClearColor(Color.CORAL);
		
		test3 = new Box2DCamera(world, batch, 2*w+5, 5, w-10, h-10, box2Pixel);
		test3.getCamera().position.setZero();
		test3.setGlClearColor(Color.FIREBRICK);
		
		test4 = new Box2DCamera(world, batch, 5, h+5, w-10, h-10, box2Pixel);
		test4.getCamera().position.setZero();
		test4.setGlClearColor(Color.FOREST);
		
		test5 = new Box2DCamera(world, batch, w+5, h+5, w-10, h-10, box2Pixel);
		test5.getCamera().position.setZero();
		test5.setGlClearColor(Color.LIGHT_GRAY);
		
		test6 = new Box2DCamera(world, batch, 2*w+5, h+5, w-10, h-10, box2Pixel);
		test6.getCamera().position.setZero();
		test6.setGlClearColor(Color.OLIVE);
		
		InputMultiplexer multiplexer = new InputMultiplexer();		
		Gdx.input.setInputProcessor(multiplexer);
		
		multiplexer.addProcessor(test1);
		multiplexer.addProcessor(test2);
		multiplexer.addProcessor(test3);
		multiplexer.addProcessor(test4);
		multiplexer.addProcessor(test5);
		multiplexer.addProcessor(test6);
		
		createBodies();
	}
	
	private void createBodies(){
		Sprite sprite = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
		sprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		sprite.setOriginCenter();
		
		BodyDef bodyDef = new BodyDef(); 
		bodyDef.position.set(0, 0);
		bodyDef.type = BodyType.DynamicBody;
		
		PolygonShape shape = new PolygonShape();
		shape.set(new float[]{
				-sprite.getWidth()/2 * pixel2Box, -sprite.getHeight()/2 * pixel2Box,
				sprite.getWidth()/2 * pixel2Box, -sprite.getHeight()/2 * pixel2Box,
				sprite.getWidth()/2 * pixel2Box, sprite.getHeight()/2 * pixel2Box,
				-sprite.getWidth()/2 * pixel2Box, sprite.getHeight()/2 * pixel2Box
		});		
		
		FixtureDef def = new FixtureDef();
		def.shape = shape;
		
		for (int i = 0; i < 25; i++){		
			Body body = world.createBody(bodyDef);
			body.setUserData(new RenderSprite(sprite, body, i));	
			body.createFixture(def);
		}		
		shape.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		
		test1.update();
		test1.render();
		
		test2.update();
		test2.render();
		
		test3.update();
		test3.render();
		
		test4.update();
		test4.render();
		
		test5.update();
		test5.render();
		
		test6.update();
		test6.render();
		
		world.step(1/60f, 6, 2);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
	
	public static class RenderSprite implements RenderItem{

		private final Sprite sprite;
		private final Body body;
		private final int z;
		public RenderSprite(Sprite sprite, Body body){
			this.sprite = sprite;
			this.body = body;
			this.z = 0;
		}
		
		public RenderSprite(Sprite sprite, Body body, int z){
			this.sprite = sprite;
			this.body = body;
			this.z = z;
		}
		
		@Override
		public void render(SpriteBatch batch, float deltaTime) {
			sprite.setCenter(body.getPosition().x * box2Pixel, body.getPosition().y * box2Pixel);
			sprite.setRotation((body.getAngle())* MathUtils.radiansToDegrees + 90);
			sprite.draw(batch);
		}

		@Override
		public int getZ() {
			return z;
		}
		
		private long id;

		@Override
		public long getId() {
			return id;
		}

		@Override
		public void setId(long id) {
			this.id = id;
		}
		
	}
}
