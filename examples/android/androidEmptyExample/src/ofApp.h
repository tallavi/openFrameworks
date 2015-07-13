#pragma once

#include "ofMain.h"
#include "ofxAndroid.h"

class ofApp : public ofxAndroidApp {
	
private:

	ofImage myImage;

	public:

		ofApp(){ofLogNotice("ofApp") << "CTOR";}
		virtual ~ofApp(){ofLogNotice("ofApp") << "DTOR";}
		
		virtual void setup() override;
		virtual void update() override;
		virtual void draw() override;
		virtual void exit() override;
		
		virtual void keyPressed(int key) override;
		virtual void keyReleased(int key) override;
		virtual void windowResized(int w, int h) override;

		virtual void touchDown(int x, int y, int id) override;
		virtual void touchMoved(int x, int y, int id) override;
		virtual void touchUp(int x, int y, int id) override;
		virtual void touchDoubleTap(int x, int y, int id) override;
		virtual void touchCancelled(int x, int y, int id) override;
};
