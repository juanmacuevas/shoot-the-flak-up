Shoot The Flak Up 
=================
Simple tank shooting game for Android.
This document describes the design and implementation process of a tank shooting game for the Android mobile platform. The following sections shows the features included in the game, the creation process and the software used during the task.

Main Features included:
-  Tank gun barrel angle & power adjustable on real-time.
-  2D graphic resources.
-  Bullets with realistic trajectory of a projectile.
-  Sound effects and background music
-  Multiple screen support (pixel density independent)
-  Moving targets with variable trajectories
-  Vibration on impacts

Process of design and implementation
------------------------------------
The creation of the application was based on an iterative and incremental development process divided into six phases. During each phase, design modifications were made, functional capabilities were added and the results were tested. Feedback from beta testers (mainly friends and family) was really useful. The whole process took five days to be done

Iteration one. Base system
--------------------------
The first step was to set some basic features of the application. Based on the needs of the task and my previous experience with 2D graphics drawing, and due to the high learning curve for developing 3D games, I chose to create the game using 2D graphics.
During this iteration the basic application skeleton of a 2D game was created by using a thread (GameThread.java) to draw the graphics on a SurfaceHolder, as it’s recommended by the Android developer’s guide for such task. This thread runs the main loop of the game that will constantly process the input, update the state of the elements, and show the graphics interface to the player.
In order to improve the performance of the game and avoid interruptions from the input, an input pipeline was implemented with a queue of input events (InputObject.java) that never blocks the main thread.

Iteration two. Tank and HUD
---------------------------
Once the base system was working, functional classes for the tank and the HUD (Head-Up Display) were created. Each of them contains the information to draw themselves on the screen using graphical functions. The position of all the objects are relative for displaying them properly on different screen dimensions. An scale factor was applied to every distance to assure the same real size independently from the pixel density of the screen.
At the end of this iteration the power bar and the tank appeared on the screen. The gun barrel was set to point on the direction where the last touch event was detected, on real-time. The power bar size indicates the shooting power depending on the time pressing before release the finger from the screen. A virtual shooting button was drawn at first, but removed later on. 

Iteration three. Shooting bullets
---------------------------------
The functional class for the bullets was created having the data to calculate its position at any moment depending on the initial angle and speed (power). The functions below describes the trajectory of the projectile.

![Bullet position equation](http://i45.tinypic.com/o74nit.png)

The bullets are added to a list when a new “MOVE_UP” event is triggered. That list is updated on every loop of the game to remove the old bullets.

Iteration four. Moving targets
------------------------------
During this phase target objects where added (FunctionalAircraft.java). Initially they had a fixed position while the impact detection was implemented. This detection is based on the distance between the center of the bullet and target.
Later on, the behaviour of the aircraft was modified to move along the sky. The trajectory of the movement is also a parabolic function like the one showed above, but reversed. This trajectory function changes when the aircraft is hit, making it fall to the ground. The initial position and direction of the aircraft are randomly assigned. New planes are generated in a constant period. Every time one of them is hit, a new one is created, making more difficult to shoot all of them.

Iteration five. Sound and vibration
-----------------------------------
The SoundManager class was created to perform the playing of the background music and the sound effects related with shoots and impacts. At the same time, a vibration command was added to produce a vibration on the phone every time a bullet hits an aircraft. 

Iteration six. Graphic resources
--------------------------------
During the last phase the graphics of all the elements were replaced with images. War game sprites were picked from Internet and modified to fit the needs of the game. This required some changes in the code. For instance, the air-crafts were drawn as circles previously, so the drawing method had to be modified to rotate and show a natural movement to the direction of the planes.


Software used in the process
----------------------------
All the code of the application was written using the Eclipse IDE for Java developers, with the Android Development Toolkit plug-in.
Either the emulator available within the Android SDK and a real device (Nexus one) were used to test the application on every stage.
Audacity was used to adapt the sounds available in the game. The original sound effects files were picked from Internet and adapted in length and bit-rate. The drums at the background belongs to the track #10 of the Full Metal Jacket original soundtrack. All the sounds are OGG files.
Photo-Shop and Gimp were the tools used to make adaptations in the image resources, such as scaling, filtering, cropping, and so on. All the images are PNG files.