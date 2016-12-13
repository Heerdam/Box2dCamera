package com.heerbann.box2dcamera;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Creates a new Box2DCamera. A Box2DCamera queries a Box2D {@link World} for bodies with a {@link Body#getUserData()} containing a class implementing {@link RenderItem}
 * All Objects not implementing the interface or null will be silently ignored.<br>
 * @author Heerbann
 */
public class Box2DCamera extends InputAdapter{

	/**
	 * An implementation of viewport for moving it at will
	 * @author Heerbann
	 */
	private class InteractiveViewport extends ScreenViewport{
			
		public InteractiveViewport(OrthographicCamera camera){
			super(camera);
		}

		public void update (boolean centreCamera) {
			setScreenBounds(getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
			setWorldSize(getScreenWidth() * getUnitsPerPixel(), getScreenHeight() * getUnitsPerPixel());
			apply(centreCamera);
		}

	}
	
	/**
	 * The interface which needs to be implemented by the class added as userdata to a body
	 * @author Heerbann
	 */
	public interface RenderItem{
		/**
		 * Method to render a sprite or something else on a body
		 * @param batch
		 * @param deltaTime
		 */
		public void render(SpriteBatch batch, float deltaTime);
		/** Defines the z-order of the cache. lower indices are getting called before high ones. can be <=0 */
		public int getZ();
		/** Used internally to avoid bodies getting cached multiple times. needs to be implemented as in private long id; */
		public long getId();
		/** Used internally to avoid bodies getting cached multiple times. needs to be implemented as in private long id; */
		public void setId(long id);
	}
	
	private final World world;
	private final SpriteBatch batch;
	private final InteractiveViewport viewport;
	private final float unitsPerPixel;
	
	/**
	 * Creates a new Box2DCamera. A Box2DCamera queries a Box2D {@link World} for bodies with a {@link Body#getUserData()} containing a class implementing {@link RenderItem}
	 * All Objects not implementing the interface or null will be silently ignored.<br>
	 * @param world The {@link World}
	 * @param batch the {@link SpriteBatch}. If more than one Box2DCamera exists the batch should be shared.
	 * @param screenX the x lower left corner of the viewport in screen coordinates
	 * @param screenY the y lower left corner of the viewport in screen coordinates
	 * @param screenWidth the width of the viewport in screen coordinates
	 * @param screenHeight the height of the viewport in screen coordinates
	 * @param unitsPerPixel how many pixels equals 1 unit in the box2d world. 
	 */
	public Box2DCamera(World world, SpriteBatch batch, int screenX, int screenY, int screenWidth, int screenHeight, float unitsPerPixel){
		this.world = world;
		this.batch = batch;
		this.viewport = new InteractiveViewport(new OrthographicCamera());
		this.viewport.setScreenBounds(screenX, screenY, screenWidth, screenHeight);
		this.viewport.update(true);
		this.unitsPerPixel = 1/unitsPerPixel;
		updateScissors();
	}
	
	/**
	 * Creates a new Box2DCamera. A Box2DCamera queries a Box2D {@link World} for bodies with a {@link Body#getUserData()} containing a class implementing {@link RenderItem}
	 * All Objects not implementing the interface or null will be silently ignored.<br>
	 * @param world The {@link World}
	 * @param batch the {@link SpriteBatch}. If more than one Box2DCamera exists the batch should be shared.
	 * @param screenWidth the width of the viewport in screen coordinates
	 * @param screenHeight the height of the viewport in screen coordinates
	 * @param unitsPerPixel how many pixels equals 1 unit in the box2d world. 
	 */
	public Box2DCamera(World world, SpriteBatch batch, int screenWidth, int screenHeight, float unitsPerPixel){
		this(world, batch, 0, 0, screenWidth, screenHeight, unitsPerPixel);
	}
	
	/**
	 * Creates a new Box2DCamera. A Box2DCamera queries a Box2D {@link World} for bodies with a {@link Body#getUserData()} containing a class implementing {@link RenderItem}
	 * All Objects not implementing the interface or null will be silently ignored.<br> 
	 * @param world The {@link World}
	 * @param batch the {@link SpriteBatch}. If more than one Box2DCamera exists the batch should be shared.
	 * @param unitsPerPixel how many pixels equals 1 unit in the box2d world. 
	 */
	public Box2DCamera(World world, SpriteBatch batch, float unitsPerPixel){
		this(world, batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), unitsPerPixel);
	}
	
	
	private void updateScissors(){
		scissors.set(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
	}
	
	/**
	 * Sets the bounds of the viewport.
	 * @param screenX the x lower left corner of the viewport in screen coordinates
	 * @param screenY the y lower left corner of the viewport in screen coordinates
	 * @param screenWidth the width of the viewport in screen coordinates
	 * @param screenHeight the height of the viewport in screen coordinates
	 */
	public void setBounds(int screenX, int screenY, int screenWidth, int screenHeight){
		viewport.setScreenBounds(screenX, screenY, screenWidth, screenHeight);
		updateScissors();
	}
	
	/** 
	 * @param screenX the x lower left corner of the viewport in screen coordinates
	 */
	public void setScreenX(int screenX){
		viewport.setScreenX(screenX);
		updateScissors();
	}
	
	/**
	 * @return the x lower left corner of the viewport in screen coordinates
	 */
	public int getScreenX(){
		return viewport.getScreenX();
	}
	
	/**
	 * @param screenY the y lower left corner of the viewport in screen coordinates
	 */
	public void setScreenY(int screenY){
		viewport.setScreenY(screenY);
		updateScissors();
	}
	
	/**
	 * @return the y lower left corner of the viewport in screen coordinates
	 */
	public int getScreenY(){
		return viewport.getScreenY();
	}

	/**
	* @param screenWidth the width of the viewport in screen coordinates
	 */
	public void setScreenWith(int screenWidth){
		viewport.setScreenWidth(screenWidth);
		updateScissors();
	}
	
	/**
	 * @return screenWidth the width of the viewport in screen coordinates
	 */
	public int getScreenWidth(){
		return viewport.getScreenWidth();
	}

	/**
	  * @param screenHeight the height of the viewport in screen coordinates
	 */
	public void setScreenHeight(int screenHeight){
		viewport.setScreenHeight(screenHeight);
		updateScissors();
	}
	
	/**
	 * @return the height of the viewport in screen coordinates
	 */
	public int getScreenHeight(){
		return viewport.getScreenHeight();
	}
	
	/**
	 * @return the viewport used by this {@linkBox2DCamera}
	 */
	public InteractiveViewport getViewPort(){
		return viewport;
	}
	
	/**
	 * @return the {@link OrthographicCamera} used by this {@linkBox2DCamera}
	 */
	public OrthographicCamera getCamera(){
		return (OrthographicCamera) viewport.getCamera();
	}
	
	/**
	 * Warning: don't call this when sharing one batch among multiple cameras!!
	 */
	public void dispose(){
		batch.dispose();
	}
	
	private boolean clearBackground = true;
	private final Rectangle scissors = new Rectangle();
	private Color glClearColor = Color.WHITE;
	
	/**
	 * Rebuilds the cache and clears the background if clearBackground == true. Colour can be set with {@link #setGlClearColor(Color)}
	 */
	public void update(){
		this.viewport.update(false);		
		if(clearBackground){
			ScissorStack.pushScissors(scissors);
			Gdx.gl.glClearColor(glClearColor.r, glClearColor.g, glClearColor.b, glClearColor.a);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			ScissorStack.popScissors();
		}
		rebuildCache();
	}
	
	private final RenderItemSort comperator = new RenderItemSort();
	private final QueryCallback callback = new QueryCallback(){

		@Override
		public boolean reportFixture(Fixture fixture) {
			Object o = fixture.getBody().getUserData();
			if(o == null) return true;
			if(!RenderItem.class.isAssignableFrom(o.getClass())) return true;
			RenderItem r = (RenderItem) o;
			if(r.getId() == Gdx.graphics.getFrameId()) return true;
			r.setId(Gdx.graphics.getFrameId());
			renderCache.add(r);
			return true;
		}
		
	};
	
	private void rebuildCache(){
		renderCache.clear();
		OrthographicCamera camera = (OrthographicCamera) viewport.getCamera(); 
		float width = (camera.viewportWidth*camera.zoom) / 2 * 1.1f;
		float height = (camera.viewportHeight*camera.zoom) / 2 * 1.1f;
		float x1 = (camera.position.x - width)  * unitsPerPixel;
		float y1 = (camera.position.y - height) * unitsPerPixel;
		float x2 = (camera.position.x + width) * unitsPerPixel;
		float y2 = (camera.position.y + height) * unitsPerPixel;
		world.QueryAABB(callback, x1, y1, x2, y2);
		if(renderCache.size > 0) renderCache.sort(comperator);
	}
	
	private final Array<RenderItem> renderCache = new Array<RenderItem>();
	
	/**
	 * Renders the cache and draws the debug lines if isDebug == true
	 */
	public void render(){
		if(renderCache.size == 0) return;
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();
		for(int i = 0; i < renderCache.size; i++){
			RenderItem r = renderCache.get(i);		
			r.render(batch, Gdx.graphics.getDeltaTime());
			r.setId(0);
		}
		batch.end();
		if(isDebug){
			OrthographicCamera c = (OrthographicCamera) viewport.getCamera();
			debugCamera.position.set(c.position).scl(unitsPerPixel);
			debugCamera.viewportWidth = c.viewportWidth * unitsPerPixel;
			debugCamera.viewportHeight = c.viewportHeight * unitsPerPixel;
			debugCamera.zoom = c.zoom;
			debugCamera.update();
			debugRenderer.render(world, debugCamera.combined);
		}
	}
	
	/**
	 * @return the colour used to clear the background of this {@link Box2DCamera}
	 */
	public Color getGlClearColor() {
		return glClearColor;
	}

	/**
	 * @param glClearColor the colour to clear the background of this {@link Box2DCamera}
	 */
	public void setGlClearColor(Color glClearColor) {
		this.glClearColor = glClearColor;
	}

	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera debugCamera;
	private boolean isDebug = false;
	
	/**
	 * Initialises the {@link Box2DDebugRenderer} and draws the debug lines of the bodies
	 */
	public void setDebug(boolean debug){
		if(debugRenderer == null){
			debugRenderer = new Box2DDebugRenderer();
			debugCamera = new OrthographicCamera();
		}
		isDebug = debug;
	}
	
	private class RenderItemSort implements Comparator<RenderItem>{

		@Override
		public int compare(RenderItem r1, RenderItem r2) {
			return (r1.getZ() < r2.getZ())?-1:(r1.getZ() > r2.getZ())?1:0;
		}
		
	}
	
	public static int panButton = Buttons.LEFT;
	
	private boolean panLockX = false, panLockY = false;
	private boolean zoomLock;
	private float zoomIncrement = 0.25f, minZoom = 1, maxZoom = 10;
	
	/**
	 * Translates the camera (not the viewport) by the given delta
	 * @param dx
	 * @param dy
	 */
	public void translateCamera(float dx, float dy){
		OrthographicCamera c = (OrthographicCamera) viewport.getCamera();
		c.translate((panLockX)?0:dx*c.zoom, (panLockY)?0:dy*c.zoom, 0);
	}
	
	/**
	 * Sets the camera (and not the viewport) to the given position
	 * @param x
	 * @param y
	 */
	public void setCameraPosition(float x, float y){
		viewport.getCamera().position.x = x;
		viewport.getCamera().position.y = y;
	}
	
	private void zoomCamera(int direction){
		OrthographicCamera c = (OrthographicCamera) viewport.getCamera();
		c.zoom += direction*zoomIncrement;
		c.zoom = (c.zoom > maxZoom)?maxZoom:(c.zoom < minZoom)?minZoom:c.zoom;
	}
	
	/**
	 * Sets the camera to the given zoom. (<=1 closest; 1000 max out)
	 * @param newZoom
	 */
	public void setCameraZoom(float newZoom){
		((OrthographicCamera) viewport.getCamera()).zoom = (newZoom > maxZoom)?maxZoom:(newZoom < minZoom)?minZoom:newZoom;
	}
	
	/**
	 * @return the current zoom of the camera
	 */
	public float getCameraZoom(){
		return ((OrthographicCamera) viewport.getCamera()).zoom;
	}
	
	private final Vector2 mouseWorldPosition = new Vector2(), mouseScreenPosition = new Vector2();
	private boolean isMouseOver = false;
	
	private void calculateMouseOver(int screenX, int screenY){
		isMouseOver = viewport.getScreenX() <= screenX && viewport.getScreenX() + viewport.getScreenWidth() >= screenX && viewport.getScreenY() <= screenY && viewport.getScreenY() + viewport.getScreenHeight() >= screenY;
	}
	
	/**
	 * @return the vector of the mouse position in world coordinates. it is updated every frame.
	 */
	public Vector2 getMouseWorldPosition() {
		return mouseWorldPosition;
	}
	
	/**
	 * @return the vector of the mouse position in screen coordinates. it is updated every frame.
	 */
	public Vector2 getMouseScreenPosition() {
		return mouseScreenPosition;
	}
	
	/**
	 * @return if the mouse pointer is currently over the viewport
	 */
	public boolean isMouseOver(){
		return isMouseOver;
	}
	
	/**
	 * Projects the given point to viewport coordinates
	 * @param input
	 * @return input
	 */
	public Vector2 projectScreenToVirtual(Vector2 input){
		return input.sub(viewport.getScreenWidth()/2, viewport.getScreenHeight()/2).sub(viewport.getScreenX(), viewport.getScreenY()).scl(1, -1);
	}
	
	/**
	 * unprojects the given vector
	 * @param input
	 * @return
	 */
	public Vector2 unprojectCoordinates(Vector2 input){
		return viewport.unproject(input);
	}
	
	private final Vector2 oldPosition = new Vector2(), deltaPan = new Vector2();
	private boolean override = false;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {	
		override = isMouseOver() && panButton == button;
		oldPosition.set(screenX, screenY);
		calculateMouseOver(screenX, Gdx.graphics.getHeight() - screenY);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		oldPosition.set(screenX, screenY);
		override = false;
		calculateMouseOver(screenX, Gdx.graphics.getHeight() - screenY);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {	
		calculateMouseOver(screenX, Gdx.graphics.getHeight() - screenY);
		projectScreenToVirtual(mouseScreenPosition.set(screenX, screenY));
		unprojectCoordinates(mouseWorldPosition.set(screenX, screenY));
		if(override){
			deltaPan.set(screenX, screenY).sub(oldPosition).scl(-1, 1);
			oldPosition.set(screenX, screenY);
			translateCamera(deltaPan.x, deltaPan.y);
			return true;
		}
		return false;	
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		calculateMouseOver(screenX, Gdx.graphics.getHeight() - screenY);
		projectScreenToVirtual(mouseScreenPosition.set(screenX, screenY));
		unprojectCoordinates(mouseWorldPosition.set(screenX, screenY));
		return false;	
	}
	
	@Override
	public boolean scrolled(int amount) {
		if(isMouseOver() && !zoomLock){		
			zoomCamera(amount);
			return true;
		}
		return false;
	}
}
