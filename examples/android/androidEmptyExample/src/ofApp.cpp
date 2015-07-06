#include "ofApp.h"
#include "ofLog.h"

int x = 0;
int y = 0;

//--------------------------------------------------------------
void ofApp::setup(){
	reloadTextures();
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
	if(x == 50)
		ofLogNotice("BUG")<<"draw";
	myImage.draw(x,y);
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

void ofApp::exit(){
//	myTexture.clear();
//	myImage.clear();
	ofLogNotice("BUG")<<"exit";
}

////--------------------------------------------------------------
//void ofApp::swipe(ofxAndroidSwipeDir swipeDir, int id){
//
//}

////--------------------------------------------------------------
//void ofApp::pause(){
//	ofLogNotice("BUG")<<"pause";
//}
//
////--------------------------------------------------------------
//void ofApp::stop(){
//	ofLogNotice("BUG")<<"stop";
//}
//
////--------------------------------------------------------------
//void ofApp::resume(){
//	ofLogNotice("BUG")<<"resume";
//}

//--------------------------------------------------------------
//void ofApp::unloadTextures(){
//	ofLogNotice("BUG")<<"unloadTextures111";
//	myTexture.clear();
//	myImage.clear();
//	ofLogNotice("BUG")<<"unloadTextures";
//}
//
//--------------------------------------------------------------

void ofApp::unloadTextures() {
	ofLogNotice("BUG")<<"unloadTextures start";
	myTexture.clear();
	myImage.clear();
	ofLogNotice("BUG")<<"unloadTextures END";
}

void ofApp::reloadTextures(){
	ofLogNotice("BUG")<<"reloadTextures start";
	ofLoadImage(myTexture,"white.png");
	ofLoadImage(myImage, "white.png");
	myImage.resize(50, 50);
	ofLogNotice("BUG")<<"reloadTextures END";
}
//
////--------------------------------------------------------------
//bool ofApp::backPressed(){
//	return false;
//}
//
////--------------------------------------------------------------
//void ofApp::okPressed(){
//
//}
//
////--------------------------------------------------------------
//void ofApp::cancelPressed(){
//
//}
