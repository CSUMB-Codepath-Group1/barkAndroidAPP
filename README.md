# BARK - Social Media App

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview

### Description
Meet up with other dogs by creating a post of an event happening and matching them with a possible owner. Could be potentially used as a way to find lost dogs, used for other pets, and for helping a pet in need of an emergency by possibly donating.

### App Evaluation
- **Category:** Photo & Video / Social 
- **Mobile:** Website is view only, uses camera, mobile first experience.
- **Story:** Allows users to share their dogs with  pictures and create events.
- **Market:** Anyone with a pet dog could enjoy this app. Ability to view posts on other dogs of the app.
- **Habit:** Users can post throughout the day many times. Users can explore endless pictures of dogs from other members of bark. Very habbit forming!
- **Scope:** Bark will start with just posting pics, creating events and viewing feeds.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**
* Allow users to sign up for a new account or login to an existing account
* Allow users to edit their profile (things such as: name, pets, location)
* Have a feed that shows users a feed of posts
* Allow users to create a new post
* Allow users to find nearby events made by other users 
* Allow social feature for users to add eachother

**Optional Nice-to-have Stories**

* Customize user feeds to only show posts made by friends.
* Show similar dog breeds.
* Ability to like and comment on user's post.
* Add option for private profiles.
* Implementing a 'guest' account with limited functionality

### 2. Screen Archetypes

* Login/Sign up page
   * The User will be able to login/Sign up
   * Use Google loging authentication
   * Users will have/create an account
   * Displays navigation bar at the bottom
* Feed Page
   * User will be able to scroll through friends feed/posts
   * User will have the ability to search someone up
   * Posts will show the owners image, username, caption, and image
   * User will be able to like/comment posts
   * User will be able to create a post
   * Displays navigation bar at the bottom
* Create Post Page
    * User will be able to create a post
    * Post will show in their feed and friends feed
    * Displays navigation bar at the bottom
* Profile Page
    * Displays users and pets images
    * Displays users description
    * Displays wanted information
    * Displays navigation bar at the bottom
* Events Page
    * User can create an event
    * Feed of nearby/friends events will display
    * Event will include users picture, name, event information
    * Others could join/invite
    * Maybe add map view of location
* Create Event Page
    * User will be able to post an event
    * Event will include location and information about events

### 3. Navigation

**Flow Navigation**

* Login / Sign up 
   * Feed
* Feed 
   * Event Page
   * Profile
   * Create Post
* Event 
   * Feed
   * Profile
   * Create Event
* Profile 
   * Feed
   * Event


## Wireframes

![barkWireframe](barkWireframe.png)



## Schema 

### Models
#### Post

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| image author |
   | image         | File     | image that user posts |
   | caption       | String   | image caption by author |
   | createdAt     | DateTime | date when post is created (default field) |

   
#### Event

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| image author |
   | description       | String   | event description by author |
   | createdAt     | DateTime | date when even is created author |
   | eventDate     | DateTime | date when event is taking place|
### Networking
#### List of network requests by screen
   - Home Feed Screen
      - (Read/GET) Query all posts where user is author
         ```java
         protected void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG,"Issue with getting posts",e);
                }
                for(Post post : posts){
                    Log.i(TAG,"Post: "+ post.getDescription());

                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }

        });
         ```
         
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
 - Home Event Screen
      - (Read/GET) Query all posts where user is author
         ```java
         protected void queryEvent() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Event.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null){
                    Log.e(TAG,"Issue with getting events",e);
                }
                for(Event event : events){
                    Log.i(TAG,"Event: "+ event.getDescription());

                }
                allEvents.addAll(events);
                adapter.notifyDataSetChanged();
            }

        });
    }
         ```
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
  - Create Post Screen
      - (Create/POST) Create a new post object
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
  - Create Event Screen
      - (Create/POST) Create a new post object
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
  - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image
      
[OPTIONAL: List endpoints if using existing API such as Yelp]
