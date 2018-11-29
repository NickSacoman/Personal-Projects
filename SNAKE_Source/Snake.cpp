#include "pch.h"
#include <iostream>
#include <conio.h>
#include <Windows.h>
using namespace std;
/**
* Basic snake game, O represents head of snake while the increasing tail is represented by o's.
* Collect the F - fruits to grow the snake and increase the score until game over.
**/
bool gameOver;

// Game board
const int width = 20;
const int height = 20;

// Game component positions
int x, y, fruitXPos, fruitYPos, score;

// Tail tracking components
int tailX[100], tailY[100];
int nTail;

// Directional snake components
enum eDirection { STOP = 0, LEFT, RIGHT, UP, DOWN };
eDirection dir;

/**
*Game setup, initialize all starting snake game compononents
**/
void Setup() 
{
	// Init game, starting without direction
	gameOver = false;
	dir = STOP;

	// Begin in center
	x = width / 2;
	y = height / 2;

	// Fruit random starting pos
	fruitXPos = rand() % width;
	fruitYPos = rand() % height;

	// Init score, displayed bottom
	score = 0;
}

/**
* Draw the game board components, snake and fruits
**/
void Draw() 
{
	system("cls"); // Clears map

	// Init top border
	for (int i = 0; i < width+2; i++) 
	{
		cout << "#";
	}
	cout << endl;

	// Mid-section game board, containing game components and borders
	for (int i = 0; i < height; i++)
	{
		for (int j = 0; j < width; j++)
		{
			if (j == 0)
				cout << "#";	// Borders
			if (i == y && j == x)
				cout << "O";	// Snake head
			else if (i == fruitYPos && j == fruitXPos)
				cout << "F";	// Fruit location
			else
			{
				bool tailTracker = false;
				for (int k = 0; k < nTail; k++)
				{
					if (tailX[k] == j && tailY[k] == i)
					{
						cout << "o";	// Tail addition
						tailTracker = true;
					}
				}
				if (!tailTracker) {
					cout << " ";	// Valid play area
				}
			}
			if (j == width - 1)
				cout << "#";	// Borders
		}
		cout << endl;
	}

	// Init bottom border
	for (int i = 0; i < width+2; i++) 
	{
		cout << "#";
	}
	cout << endl;

	// Score area
	cout << "Score: " << score << endl;
}

/**
* Input game controls, wasd keys used for snake controls (q is quit)
**/
void Input() 
{
	if (_kbhit())
	{
		switch (_getch())
		{
		case 'a' :
			dir = LEFT;
			break;
		case 'd':
			dir = RIGHT;
			break;
		case 's':
			dir = DOWN;
			break;
		case 'w':
			dir = UP;
			break;
		case 'q':
			gameOver = true;
			break;
		}
	}
}

/**
* Game logic components, mainly for collision detections and tail tracking
**/
void Logic() 
{
	// Directional handling and tail control
	int prevTailX = tailX[0];
	int prevTailY = tailY[0];
	int prev2X, prev2Y;
	tailX[0] = x;
	tailY[0] = y;
	for (int i = 1; i < nTail; i++)
	{
		prev2X = tailX[i];
		prev2Y = tailY[i];
		tailX[i] = prevTailX;
		tailY[i] = prevTailY;
		prevTailX = prev2X;
		prevTailY = prev2Y;
	}
	switch (dir)
	{
	case LEFT:
		x--;
		break;
	case RIGHT:
		x++;
		break;
	case UP:
		y--;
		break;
	case DOWN:
		y++	;
		break;
	}

	/** Use if borders cause loss **/
	//if (x > width || x < 0 || y > height || y < 0)
	//	gameOver = true;

	// Out of bounds handling
	if (x >= width)
		x = 0;
	else if (x < 0)
		x = width - 1;
	if (y >= height)
		y = 0;
	else if (y < 0)
		y = height - 1;

	// Tail collision
	for (int i = 0; i < nTail; i++)
	{
		if (tailX[i] == x && tailY[i] == y)
		{
			gameOver = true;
		}
	}

	// Fruit handling
	if (x == fruitXPos && y == fruitYPos)
	{
		score += 10;
		fruitXPos = rand() % width;
		fruitYPos = rand() % height;
		nTail++;
	}
}

/**
Main Runner
**/
int main()
{
	// Set up game components
	Setup();

	// Gameplay
	while (!gameOver) {
		Draw();
		Input();
		Logic();
		Sleep(10);	// Slows game, adjust for easier settings
	}
}
