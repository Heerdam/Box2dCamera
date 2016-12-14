# Box2dCamera
Simple render-helper class for Box2D and libgdx

Box2dCamera is simple class for libgdx to query and draw bodies in a Box2D world. This is meant for beginners with much experience yet. This camera allows the use of large box2d worlds with almost unlimited static or sleeping bodies without worrying about huge render queues. In short: this camera only iterates and draws what it sees.

### Features
* Individual viewport size and position
* Individual clearing colour (or none)
* Use as many as you need. There are no restrictions.
* Only draws what it sees. Have as many bodies as you need.
* Built in zoom and pan support out of the box.
* Various helper methods to unlock the full functionality


### Six Box2dCameras draw the same scene with different clearing color and zoom
![pic](https://i.imgur.com/zUxZHIX.png)

### How to use it

##### Step 1: Create the camera(s) and set them up. Remember that only ever one SpriteBatch is needed.

``` java
world = new World(new Vector2(0, 0), true); 
		batch = new SpriteBatch();
		
		int w = (int)(Gdx.graphics.getWidth() / 3);
		int h = (int)(Gdx.graphics.getHeight() / 2);
		
		test1 = new Box2DCamera(world, batch, 5, 5, w-10, h-10, box2Pixel);
		//test1.setDebug(true);
		test1.getCamera().position.setZero();
		test1.setGlClearColor(Color.CHARTREUSE)
    ///repeat for the 5 others
```
##### Step 2: Add the cameras to an InputMultiplexer to activate inputs (zoom and pan).

```java
InputMultiplexer multiplexer = new InputMultiplexer();		
		Gdx.input.setInputProcessor(multiplexer);
		
		multiplexer.addProcessor(test1);
		multiplexer.addProcessor(test2);
		multiplexer.addProcessor(test3);
		multiplexer.addProcessor(test4);
		multiplexer.addProcessor(test5);
		multiplexer.addProcessor(test6);
```

##### Step 3: Add the cameras to the render loop.

```java
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
```

##### Step 4: Create your own implementation of the RenderItem interface. This are the objects you add to the userdata of the body (not the fixture).

```java
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
```

##### Step 5: Create some bodies to render:

```java
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
```
