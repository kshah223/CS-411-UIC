#CS 411 - Assignment 3
#Breadth First Search on 15 Puzzle
#Kalpkumar Shah

# Libraries which is used to define the things in program
import random
import math
import time
import psutil
import os
from collections import deque

#We start this class which defines the problem in board 
class Board:
	def __init__(self,tiles):

		#length of the board and also width
		self.size = int(math.sqrt(len(tiles)))
		self.tiles = tiles

	# Now for taking care of the actions from the current state we need to define a function which gives us the 
	# resulting state.
	# This function takes care of it
	def actions(self,action):
		new_tiles = self.tiles[:]
		empty_index = new_tiles.index('0')
		# Checks if the action is to go up
		if action=='u':
			if empty_index-self.size>=0:
				new_tiles[empty_index-self.size],new_tiles[empty_index] = new_tiles[empty_index],new_tiles[empty_index-self.size]
		
		# Checks if the action is to go down
		if action=='d':
			if empty_index+self.size < self.size*self.size:
				new_tiles[empty_index+self.size],new_tiles[empty_index] = new_tiles[empty_index],new_tiles[empty_index+self.size]

		# Checks if the action is to go left
		if action=='l':	
			if empty_index%self.size>0:
				new_tiles[empty_index-1],new_tiles[empty_index] = new_tiles[empty_index],new_tiles[empty_index-1]
		
		# Checks if the action is to go right
		if action=='r':
			if empty_index%self.size<(self.size-1): 	
				new_tiles[empty_index+1],new_tiles[empty_index] = new_tiles[empty_index],new_tiles[empty_index+1]
		return Board(new_tiles)
		

# As for the bfs here we defined a class Node which consist of parent, children and state.		
class Node:

	# initializing all the variables
	def __init__(self,state,parent,action):
		self.state = state
		self.parent = parent
		self.action = action
	
	# Returns state with the string representation
	def __repr__(self):
		return str(self.state.tiles)
	
	# Equality test for the other state and current state.
	def __eq__(self,other):
		return self.state.tiles == other.state.tiles

	# returning hash value of the state	
	def __hash__(self):
		return hash(self.state)

# Function which generates a random puzzle of 15-puzzle		
def generate_puzzle(size):
	numbers = list(range(size*size))
	random.shuffle(numbers)
	return Node(Board(numbers),None,None)

#This function returns the list of children obtained after simulating the actions on current node
def children(parent):
	children = []
	actions = ['l','r','u','d'] # left,right, up , down ; actions define direction of movement of empty tile
	for action in actions:
		child_state = parent.state.actions(action)
		child_node = Node(child_state,parent,action)
		children.append(child_node)
	return children

#This function backtracks from current node to reach initial configuration. The list of actions would constitute a solution path
def find_path(node):	
	path = []	
	while(node.parent is not None):
		path.append(node.action)
		node = node.parent
	path.reverse()
	return path

# BFS algorithm similar to aima textbook and returns path, number of nodes expanded and total time taken	
def bfs(root_node):
	start_time = time.time()
	frontier = deque([root_node])
	explored = set()
	count=0
	while(len(frontier)>0):
		cur_time =time.time()
		cur_node = frontier.popleft()
		count+=1
		explored.add(cur_node)
		if(goal_test(cur_node.state.tiles)):
			path = find_path(cur_node)
			end_time = time.time()
			return path,count,(end_time-start_time)
		for child in children(cur_node):
			if child in explored:
				continue
			else:
				frontier.append(child)
	print("frontier empty")	
	return False

# Function to check if current state is goal state or no
def goal_test(cur_tiles):
	return cur_tiles == ['1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','0']	

# Main function
# Asking the user to enter the puzzle into the console, then runnung bfs and showing output	
def main():
	userInput = psutil.Process(os.getpid())
	primary_memory = userInput.memory_info().rss / 1024.0
	primary = str(input("initial configuration: "))
	primary_list = primary.split(" ")
	root = Node(Board(primary_list),None,None)
	
	print(bfs(root))
	final_memory = userInput.memory_info().rss / 1024.0
	print(str(final_memory-primary_memory)+" KB")

if __name__=="__main__":
	main()	