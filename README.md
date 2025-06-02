# Smart Security App – Application Android de surveillance IoT

Cette application Android est conçue pour interagir avec un système de sécurité basé sur ESP32-CAM. Elle permet de recevoir des alertes d'intrusion en temps réel, d'afficher les images capturées, de gérer l'état du système de sécurité et de consulter un historique des événements via Firebase.

## Fonctionnalités principales

-  Authentification sécurisée avec Firebase Authentication
-  Affichage en temps réel des alertes envoyées par l’ESP32-CAM
-  Visualisation des images capturées via Glide
-  Horodatage précis de chaque alerte
-  Armement / désarmement du système via une clé secrète
-  Historique consultable avec RecyclerView

##  Structure de l'application

- **MainActivity** : affichage des alertes en direct
- **EventLogActivity** : consultation de l’historique des alertes
- **Login/Register** : pages de connexion utilisateur via Firebase Auth
- **Paramétrage** : gestion de la clé secrète et de l’état du système

##  Technologies utilisées

- **Langage** : Java
- **IDE** : Android Studio
- **Base de données** : Firebase Realtime Database
- **Librairies** :
  - Firebase Authentication
  - Firebase Database
  - Glide (pour l’affichage des images)
  - RecyclerView

##  Connexion avec ESP32-CAM

- L'ESP32-CAM envoie une alerte à Firebase avec :
  - l’URL de l’image capturée
  - la distance détectée
  - l’horodatage
- L'application mobile lit ces données et les affiche en temps réel.


