# Box2dCamera
Simple render-helper class for Box2D and libgdx

Box2dCamera is simple class for libgdx to query and draw bodies in a Box2D world. This is meant for beginners with much experience yet. This camera allows the use of large box2d worlds with almost unlimited static or sleeping bodies without worrying about huge render queues. In short: this camera only iterates and draws what it sees.

<h3>Features</h3>
* Individual viewport size and position
* Individual clearing colour (or none)
* Use as many as you need. There are no restrictions.
* Only draws what it sees. Have as many bodies as you need.
* Built in zoom and pan support out of the box.
* Various helper methods to unlock the full functionality


<h3>Six Box2dCameras draw the same scene with different clearing color and zoom</h3>
![pic](https://i.imgur.com/zUxZHIX.png)
