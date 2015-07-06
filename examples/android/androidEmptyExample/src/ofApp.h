#pragma once

#include "ofMain.h"
#include "ofxAndroid.h"

class ofApp : public ofxAndroidApp {
	
private:

	ofTexture myTexture;
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

		virtual void unloadTextures() override;
		virtual void reloadTextures() override;
//		virtual void swipe(ofxAndroidSwipeDir swipeDir, int id) override;
//
//		virtual void pause() override;
//		virtual void stop() override;
//		virtual void resume() override;
//		virtual void unloadTextures() override;
//		virtual void reloadTextures() override;
//
//		virtual bool backPressed() override;
//		virtual void okPressed() override;
//		virtual void cancelPressed() override;
};
