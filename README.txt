
---------------------------------------------------------------------------------------------------------------------
------------------------------------------------Game instruction-----------------------------------------------------
---------------------------------------------------------------------------------------------------------------------	

Game Objective: To get as many points and as possible as a team by answering questions.

Game Description: Answer questions related to different geographic areas to get points. In order to get as many points as possible, 
1. start with geographic areas you are more familiar with, 2. discuss with your teammate about the answers, 
3. submit score when you have answered most questions that you know the answers. When the game ends, 
you can see in the chat room whether your team won or lost.


Google Map API Key: 

1. Go to server.serverMiniMVC.serverMiniModel.msg.IStartGameMsg
Inside the make method, there is a string called MAPS_API_KEY, fill in that string with
your own Google Map API key.

2. Go to realgame.controller.RealGameController. Inside the main run() method, there is a string called MAPS_API_KEY, fill in that string with your own Google Map API key.


How to run the Server:

Step 1: Run the server controller (or ServerController (1).launch file)
Step 2: Type in server name and log in, and select category
Step 3: Let all the clients who want to play the game connect to you through discovery panel
Step 4: Click on “Send Game” button to assign clients to one of two teams.
Step 5: Click on “Start Game” button to let clients display the game.
Step 6: Play the game (More detailed information in the next section)
Step 7: Click end game button to determine which team wins.

How to run the Client:

Step 1: Run the client controller (or ClientControllerReal.launch file)
Step 2: Type in client name and log in, and select category
Step 3: Connect to the server through discovery panel
Step 4: Play the game (More detailed information in the next section)

How to end and exit the program:
The client can just leave the chatroom to leave game. The server can quit the program and the game on each client would disappear.

How to Play the Game:

Step 1: Choose a marker (better be one in a geographic area you are familiar with)
Step 2: LEFT click the marker to show questions related to the place
Step 3: RIGHT click the marker to show options to answer the question
Step 4: Discuss answers with your teammates in your team chat room
Step 5: Choose an answer by clicking on the button of that option (The marker will disappear after you choose the answer, 
you would get +2 points if the answer is right, -1 if it’s wrong)
Step 6: Go the next marker and the answer question
Step 7: Submit your score when you feel like you have got enough points
Step 8: After everyone submitted their score in both teams. Click “End Game” button in server, and the server would tell everyone 
in both team if their team won and lost. (The team that earned more points would win)
	

Highlight the CleintController.launch and ServerController.launch click the run button. The program will be started.

First, on the leftmost of the control panel, login by typing in an username and click login. Type
in a desired category (or no category) to connect to the discovery server. 

Client-Server connection:
	First login the client, and then the server.
	When getting someone from the discovery server, an client/server will show up in the Connected Users drop down.
	We can getSelectedEndPoint to connect to the server or client. Auto-connected back is implemented here,

Team creating/joining/leaving:

	Click on "Send Game" button on the server, all the connected user would be split into two teams and put in the 
	corresponsding team chat room.  
	
Sending Game:
	
	After teams are created. Click on "Start Game" button on the server to send over everyone a game.
	
Quit the program: 

	Click "End Game" on the Server to let everyone know if their team won or lost. Then you can leave the chat room to leave the game. 
	To quit the program, click the Quit button.
	
Milestone 1 Answer:
---------------------------------------------------------------------------------------------------------------------
------------Description of how our game will work, including typical game play with winning and losing---------------
---------------------------------------------------------------------------------------------------------------------

In our game, each player will guess the flight time from city A to city B, the average guess of all players in a team becomes the final 
guess of that team. The team which guessed the closest answer would add 5 points to its final score and other teams would lose 3 points. 
The team that reaches 25 points first would win the game and other teams would lose automatically. The questions are shown in the map 
as two markers with color lines in between to show the flight path. There are a sequence of questions that are implemented beforehand 
and all the team would get the same sequence of questions to ensure the fairness of the game.
 
******************* The basic classes needed to implement the game ************************
 
Server:
Server controller: controller for server main MVC
Server Model: server main model
Server View: server main view
Server Model2ViewAdapter: The adapter that allows main model to use methods in main view
Server View2ModelAdapter: The adapter that allows main view to use methods in main model
 
Server Mini Controller: controller for server mini MVC
Server Mini Model: server mini model
Sever Mini View: server mini view
Server Mini Model2ViewAdapter: The adapter that allows server mini model to use methods in mini view.
Server Mini View2ModelAdapter: The adapter that allows server mini view to use methods in mini model.
 
Server Mini2MainAdapter: The adapter that allows mini MVC to use methods in main MVC

Client:
Client controller: controller for Client main MVC
Client Model: Client main model
Client View: Client main view
Client Model2ViewAdapter: The adapter that allows main model to use methods in main view
Client View2ModelAdapter: The adapter that allows main view to use methods in main model
 
Client Mini Controller: controller for Client mini MVC
Client Mini Model: Client mini model
Client Mini View: Client mini view
Client Mini Model2ViewAdapter: The adapter that allows Client mini model to use methods in mini view.
Client Mini View2ModelAdapter: The adapter that allows Client mini view to use methods in mini model.
 
Client Mini2MainAdapter: The adapter that allows mini MVC to use methods in main MVC

Data: 
(This class is for the server and client to handle special unknown messages, like game and maps.)

IMapMessage: The map message that the server sends to the client

MapMsgCmd: The cmd to send over when the receivers don’t know how to handle a map message.

************************* The messages that the game needs to be sending around ****************************

IConnectMsg: Message for initial connection among IConnections

IRequestTeamsCollectionMsg: Message received that a client is requesting a collection of teams available to join

IReturnTeamsCollectionMsg: Message received that a client is returning a collection of teams available to join so the player knows what are the team it can join

IJoinedTeamMsg:  An interface that defines a message that a player received when someone joins the team.

IAddToTeamMsg: Server tells client they have been added to a team (for force invite).

IStringMsg: This interface defines a string message for communication among players.

IRequestCmdMsg: This interface defines a message that tells a player someone is requesting a command.

IAddCmdMsg: This interface defines a message that tells a player to add a command.

IMapMessage: The map message that the server sends to the client


**************************** What processing takes place on the client vs what processing takes place on the server (if any) *****************
 
The client would first connect to the server, and the server would send the map and the game to the client. 
The client can then create a team, or join an existing team. The process of creating a team, in our game, is done by the clients. 
The server is in charge of updating game state. Whenever there is an update for the game state made by a client, 
the server should know and then update game state for other players in the game, or in the team, depending on whether it is a global 
level update or team level update. 


**************************** The visual elements needed by the game **********************************

The map would support zoom in and out. Adding markers on the map. In addition, each question has two cities, 
marked by two markers on google map, also there is a line connecting the two markers to show you the flight route, 
giving the players a better visualization of the distance between the two cities. Each player can drop markers on the map 
and all the other players in the team could see the dropped marker. The players in the room can utilize this drop 
marker functionality to break the entire trip into several segments and further discuss the information within the team.


************** Human player interactions with the user interface and what will happen as a result *****************

Log in: enter player name and then click log in to connect to the discovery server.

Connect server and client: select the server/client and click “Get Selected Endpoint”, the current server/client would send a 
IConnectMsg to the selected server/client, the selected server/client will auto-connect back to the current server/client

Now you are connected to the discovery server and can join teams

Click on create team: the client would create a team.

Click on get teams: the client would get all the teams available from the selected player. The client would send a 
IRequestTeamsCollectionMsg to the selected player. The player would then IReturnTeamsCollectionMsg back to the requestor. 
The requestor would process the return message and add all the available teams that can join. 

Click on join team: the client would join the selected team. The client would first send a IJoinedTeamMsg to Server to 
request to join the team. The server that receives this IJoinedTeamMsg would send back a IAddToTeamMsg to the client, 
adding the client to the team.

Now you are in a team and can send text messages in the team and the server can send map to clients.

Type in text in the text field in the team. Click send to send text message in the team. Your client would send a 
IStringMsg to the team and the other players in the team would receive that well known IStringMsg.

Click on “Send Map”in the server mini view: the server would send a map to all the clients in the team. The server would send 
a IMapMessage defined by itself to the clients. This goes to the default/unknown case for the clients, since the clients 
don’t know how to handle this IMapMessage. Clients send a IRequestCmdMsg to the server to request the command to process the map. 
The server would then send IAddCmdMsg to add MapMsgCmd to tell the clients the command of how to process the IMapMessage.
 The client would save this command in the cache so they can use it later on.

******** What sorts of command-to-command communications (on the same machine) are needed and how those communications will be established ******



When a player send a unknown message defined by itself to the other players. This goes to the default/unknown case for the other players, 
since the clients don’t know how to handle this unknown message. Other players send a IRequestCmdMsg to the sender to request the command 
to process the unknown message. The sender would then send IAddCmdMsg to add MapMsgCmd to tell the other players the command of how to process the IMapMessage.
Other players would save this command in the cache so they can use it later on. Then, when there are some information that players need to 
store, you can store it and then user it later when the game command tries to read these informations. The IStoreMsg and IRetrieveMsg allows us
to get the information later on. Specifically, we decided to include IStore/IReceive messages as a fundamental component of our design. 
Moreover, we explicitly made the choice to create two versions of these messages to allow both servers and other players to process them. 
Essentially, we believe that the easiest way to process foreign commands is to hook up GUI elements to a shared database. 
The decision of whether or not to have this database centralized in the server or shared among all players belongs to the developers. 

In other words, On receiving an unknown message type, client/player A sends an IRequestCmdMsg to the sender B (client/server/player) of the message.
B, on receiving the IRequestCmdMsg, will send back an IAddMsg for the A to handle the unknown message. Note that as an API, 
we do not enforce the rule on whether A should store an algo command received from a remote sender, but it is recommended that 
received commands are stored for future use.

-------------------------------------------------------------------------------------------------------------------------------------------
------Description of how your design will implement the required common API as well as any features specific to your implementation--------
-------------------------------------------------------------------------------------------------------------------------------------------


Our API is designed to maximize the number of options the game designer has, while still providing the necessary backbone architecture 
intrinsic to any design. Below is a breakdown of the key features in our API.

The client deals with application-level interactions from the prospective player's perspective. 
A client can connect with any server on the discovery server or any other client on the discovery server. 
This maximizes the number of ways a client can connect to a game/ interact with users. This is analogous to the User from Chatapp.

A player is an extension of the client and is the object that actually interacts with games. A client can have many 
players - one for each game. This is analogous to the Chatuser from Chatapp. 

A team is merely a collection of players; therefore, to interact with a team the game designer simply iterates through 
the container that is the team, updating each player's state. All actions upon the team are game-dependent. 
This is analogous to the Chatroom from Chatapp.

The server is, in some way, the centerpiece of the game. Our API is flexible in that it allows both a game that communicates 
peer-to-peer, in which the server initiates the game then "disappears", or the traditional client-server model, 
in which the server is constantly monitoring the game. This maximizes the amount of ways a game can be created and maintained.


Our design is fundamentally based on ChatApp, with a few key changes made to tailor the application specifically to games. 
With that in mind, we made 2 explicit choices that may differ from other groups.

The first is the semantic distinction between IServer and IClient. Whereas in the original ChatApp API these two entities 
were essentially combined into IUser, for the final project we felt that they no longer serve the same role. 
A server is not going to be requesting to join a game, nor do we anticipate it connecting with other servers. 
It makes sense then, to draw a line between the machine hosting a game (IServer) and the machines playing it (IClient). 
It can be noticed that although the methods on the client and the server are really similar and they both extends IConnection, 
we believe that having 2 separate notions are good for semantic clarity. Everything on Discovery Server is an IConnection, 
but the getType() method helps distinguish between the objects so that developers can properly know who is the server 
and who is a client - which is important in knowing which message type can be sent to which object.

The second major decision stems from the desire to give developers as much flexibility as possible. 
Specifically, we decided to include IStore/IReceive messages as a fundamental component of our design. 
Moreover, we explicitly made the choice to create two versions of these messages to allow both servers and other players to 
process them. Essentially, we believe that the easiest way to process foreign commands is to hook up GUI elements to a shared database. 
The decision of whether or not to have this database centralized in the server or shared among all players belongs to the developers. 
This allows for a P2P architecture whenever possible, routing through the server only if the game demands it.


 

	