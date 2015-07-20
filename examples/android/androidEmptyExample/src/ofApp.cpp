#include "ofApp.h"
#include "ofLog.h"

int x = 0;
int y = 0;

//--------------------------------------------------------------
void ofApp::setup(){

	ofLogNotice("ofApp")<<"setup start";

#ifdef TEST_IMAGE

	myImage.load("blue.png");

#endif

	reloadTextures();

	ofLogNotice("ofApp")<<"setup end";
}

//--------------------------------------------------------------
void ofApp::update(){

	x++;
	y++;
	if(100 < x)
	{
		x = 0;
		y = 0;
	}
}

//--------------------------------------------------------------
void ofApp::draw(){
	//if(x == 50)
	//	ofLogNotice("BUG")<<"draw";

#ifdef TEST_TEXTURE

	myTexture.draw(x,y);

#endif

#ifdef TEST_IMAGE

	myImage.draw(200-x,200-y);

#endif
}

//--------------------------------------------------------------
void ofApp::keyPressed(int key){
}

//--------------------------------------------------------------
void ofApp::keyReleased(int key){ 
}

//--------------------------------------------------------------
void ofApp::windowResized(int w, int h){

}

//--------------------------------------------------------------
void ofApp::touchDown(int x, int y, int id){

}

//--------------------------------------------------------------
void ofApp::touchMoved(int x, int y, int id){

}

//--------------------------------------------------------------
void ofApp::touchUp(int x, int y, int id){

}

//--------------------------------------------------------------
void ofApp::touchDoubleTap(int x, int y, int id){

}

//--------------------------------------------------------------
void ofApp::touchCancelled(int x, int y, int id){

}

void ofApp::reloadTextures() {

	ofLogNotice("ofApp")<<"reloadTextures START";

#ifdef TEST_TEXTURE

	bool result = ofLoadImage(myTexture, "red.png");

	ofLogNotice("ofTexture") << "loading into : " << &myTexture;

	ofLogNotice("ofApp")<<"loaded";

	ofLogNotice("ofApp") << "result = " << result;

	ofLogNotice("ofApp") << "allocated = " << myTexture.isAllocated();

	ofLogNotice("ofApp") << "address = " << &myTexture;

#endif

	ofLogNotice("ofApp")<<"reloadTextures END";

}

void ofApp::exit(){
	ofLogNotice("ofApp")<<"exit";
}
