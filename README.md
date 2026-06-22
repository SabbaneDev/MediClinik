# Système de Gestion Clinique (MEDI-CLINIC)
### Projet de Programmation Java

---

## 1. Présentation du Projet
**MEDI-CLINIC** est une application de bureau conçue en **Java** et **JavaFX**, utilisant une base de données **MySQL** via **JDBC**. Le projet respecte l'architecture **MVC** (Modèle-Vue-Contrôleur) avec une séparation stricte des couches :
*   **Modèles (`models/`)** : Classes d'entités métiers (`Patient.java`, `Appointment.java`), gestion de la connexion (`DBConnection.java`) et objets d'accès aux données (DAO) sécurisés par des requêtes préparées (`PatientDAO.java`, `AppointmentDAO.java`).
*   **Vues (`views/`)** : Fichiers graphiques au format XML (`main-view.fxml`, `patient-view.fxml`, `appointment-view.fxml`) stylisés à l'aide d'un design moderne en CSS sombre.
*   **Contrôleurs (`controllers/`)** : Logique applicative de gestion des événements utilisateur et de validation des formulaires.

---

## 2. Prérequis
1.  **MySQL Server (via XAMPP)** : Assurez-vous que XAMPP est installé et que le module MySQL est démarré.
2.  **Java JDK  installé sur votre machine.
3.  Un environnement de développement (IDE) comme **IntelliJ IDEA**, **Eclipse** ou **VS Code**.

---

## 3. Configuration de la Base de Données (XAMPP MySQL)
1.  Démarrez **Apache** et **MySQL** depuis le panneau de contrôle de **XAMPP**.
2.  Ouvrez votre navigateur et accédez à [http://localhost/phpmyadmin](http://localhost/phpmyadmin).
3.  Créez une nouvelle base de données nommée `clinic_management_db`.
4.  Cliquez sur l'onglet **Importer** (Import) dans phpMyAdmin.
5.  Choisissez le fichier `schema.sql` situé à la racine de ce projet et cliquez sur **Importer**.
    *   *Alternative* : Copiez-collez le contenu de `schema.sql` dans l'onglet **SQL** de phpMyAdmin et exécutez la requête.

Le script créera automatiquement les tables `patients` et `appointments`, définira la contrainte de clé étrangère avec suppression en cascade (`ON DELETE CASCADE`), et insérera des données d'exemple pour vos tests et présentations.

---

## 4. Importation et Lancement dans votre IDE

###  Visual Studio Code
1.  Ouvrez le dossier du projet dans VS Code.
2.  Installez le pack d'extensions **Extension Pack for Java** et **JavaFX Support** si ce n'est pas déjà fait.
3.  Run `mvn clean javafx:run` 

---

## 5. Fonctionnalités Clés du Projet
*   **Tableau de Bord Intuitif** : Des cartes de statistiques mis à jour dynamiquement qui affichent le nombre total de patients, de rendez-vous enregistrés, et de consultations à venir.
*   **Gestion des Patients (CRUD)** :
    *   Enregistrement de nouveaux dossiers.
    *   Recherche filtrée en temps réel (par nom, prénom ou téléphone).
    *   Modification en un clic depuis le tableau interactif.
    *   Suppression sécurisée (efface automatiquement les rendez-vous liés).
*   **Planification des Rendez-vous (CRUD)** :
    *   Sélection fluide de patients existants via un menu déroulant.
    *   Combos intuitifs de sélection de l'heure et des minutes pour éviter les erreurs de saisie.
    *   Recherche intégrée par médecin ou nom de patient.
*   **Sécurité et robustesse** :
    *   Utilisation systématique de `PreparedStatement` pour barrer la route aux attaques par injection SQL.
    *   Gestion d'exceptions pour éviter les plantages (par exemple : vérification que l'âge saisi est un entier valide).
    *   Documentation au format standard **Javadoc** sur toutes les classes et méthodes publiques.

---