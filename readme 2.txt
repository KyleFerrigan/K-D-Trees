/******************************************************************************
 *  Name: Kyle Ferrigan
 *  NetID: kbferrigan
 *  Precept:  N/A
 *
 *  Partner Name: Patrick Galati
 *  Partner NetID: pagalati
 *  Partner Precept: N/A
 * 
 *  Operating system: Kyle: Win10; Patrick: Win10
 *  Compiler: Kyle: JetBrains IntelliJ IDEA; Patrick: Netbeans
 *  Text editor / IDE: Kyle: JetBrains IntelliJ IDEA; Patrick: Netbeans
 *
 *  Have you taken (part of) this course before:
 *  Kyle: No; Patrick: No
 *  Hours to complete assignment (optional):
 *
 ******************************************************************************/

Programming Assignment 3: K-d Trees


/* *****************************************************************************
 *  Describe the Node data type you used to implement the
 *  2d-tree data structure.
 **************************************************************************** */
We need to store the point, symbol table value, the axix-aligned rectangle, the left/bottom subtree, the right/top subtree, and the x and y coordinate (boolean).
/* *****************************************************************************
 *  Describe your method for range search in a k-d tree.
 **************************************************************************** */
By using an ArrayList of points, we need to recursively check to see which x or y level we are in and return the appropiate array list. If we are in the x-coordinate, we check the rectangle's min and max x value against the temporary traversal node. If the left/bottom or right/top ever points to null, then we can return the array list and update the temporary node appropiately. Else, we call contains and add the point to the array list if needed, else if we are checking to see which of the temporary nodes are not null. If so we update the array list to recursively call the range search again. The same is done if we are in the y-coordinate instead.

/* *****************************************************************************
 *  Describe your method for nearest neighbor search in a k-d tree.
 **************************************************************************** */
Recursively check the node and the nearest node's points and perform comparisons. If we are at the last node, then we have already found the nearest point. If the node's point is less than the nearest point, then we just set nearest to the node's point. If the nearest rectangle point is closer than the nearest point, then we can return nearest. Otherwise, we need to find the nearest by checking the x and y coordinates. This is where the recursion is done.

/* *****************************************************************************
 *  How many nearest-neighbor calculations can your PointST implementation
 *  perform per second for input1M.txt (1 million points), where the query
 *  points are random points in the unit square?
 *
 *  Fill in the table below, using one digit after the decimal point
 *  for each entry. Use at least 1 second of CPU time.
 *  (Do not count the time to read the points or to build the 2d-tree.)
 *
 *  Repeat the same question but with your KdTreeST implementation.
 *
 **************************************************************************** */


                 # calls to         /   CPU time     =   # calls to nearest()
                 client nearest()       (seconds)        per second
                ------------------------------------------------------
PointST:        8                      1.027            7.7

KdTreeST:       116852                  1.00            116852

Note: more calls per second indicates better performance.


/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */
No known bugs

/* *****************************************************************************
 *  Describe whatever help (if any) that you received.
 *  Don't include readings and lectures, but do
 *  include any help from people (including classmates and friends) and 
 *  attribute them by name.
 **************************************************************************** */
Eman B gave a hint on the use of xmin, ymin, xmax, ymax. Made visualizing the assignment easier.

/* *****************************************************************************
 *  Describe any serious problems you encountered.                    
 **************************************************************************** */
Had wrong colored nodes in boundary cases, but we were able to correct that mistake

/* *****************************************************************************
 *  If you worked with a partner, give one
 *  sentence explaining what each of you contributed.
 **************************************************************************** */
Kyle: constructors, range (and associated helper functions), points and dive (and associated helper functions).

Patrick: nearest (and associated helper functions), put (and associated helper functions), and get (and associated helper functions).

/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback   
 *  on  how helpful the class meeting was and on how much you learned 
 *  from doing the assignment, and whether you enjoyed doing it.
 **************************************************************************** */
The interactable rectangle draw feature is a nice change of pace to see in an assignment. Gave a new perspective on the code execution.


/* *****************************************************************************
 *  Include the screenshots of your output.
 **************************************************************************** */
