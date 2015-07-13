#include "ofApp.h"
#include "ofLog.h"

int x = 0;
int y = 0;

//--------------------------------------------------------------
void ofApp::setup(){

	ofLogNotice("BUG")<<"setup start";

	myImage.load("white.png");

	ofLogNotice("BUG")<<"setup end";
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
	ofLogNotice("BUG")<<"exit";
}
