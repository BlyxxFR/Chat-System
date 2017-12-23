# Système de discussion en temps réel décentralisé

### Ce qui marche

* Gestion des échanges des messages one-to-one
* Gestion de l'identification des utilisateurs connectés
* Gestion des échanges des messages one-to-many
* Historisation des échanges
* Gestions des cas particuliers (multiples lancements de l'application sur une même machine, envoi d'un message à un utilisateur qui vient de se déconnecter)
* Notification par le système lorsqu'un message est reçu (cf plus bas)

### Phase de connexion

![Interface de connexion](images/login.png)

Le mot de passe ne sert pour l'instant à rien. Si vous vous connectez avec un pseudonyme déjà utilisé sur le réseau, vous aurez un message d'erreur. 

### Pour ajouter un projet

![Fenêtre pour ajouter un projet](images/add_project.png)

Saisir le nom du projet et l'adresse IP Multicast associée.

### Notification lors de la réception d'un message

![Notification système](images/notification.png)

### Affichage d'une conversation de groupe

![Conversation de groupe - Vue de Jacquie](images/conv_jacquie.png)

Vue de Jacquie

![Conversation de groupe - Vue de Michel](images/conv_michel.png)

Vue de Michel

### Affichage d'une conversation privée

![Conversation privée - Vue de Michel](images/private_conv.png)
