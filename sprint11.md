# Sprint 11 : Instance unique des controleurs

## Le principe

Avant, `MethodExecutor.execute` faisait `newInstance()` sur la classe du controleur **a chaque requete** : 1000 requetes = 1000 objets crees puis jetes.

Maintenant chaque controleur est instancie **une seule fois** puis reutilise :

```
requete → trouver la Method (urlMap) → [AUTH/ROLE] → getControllerInstance(classe) → MethodExecutor.execute(method, instance, ...) → reponse
                                                              │
                                                              ├─ deja dans la map → on la reutilise
                                                              └─ absente → creation synchronisee puis ajout dans la map
```

---

## Les modifications

### 1. AppInitializer : la map globale
- Au demarrage (`contextInitialized`), creation d'une `ConcurrentHashMap<Class<?>, Object>`.
- Rangee dans le `ServletContext` sous la cle `"controllerInstances"`.
- `ConcurrentHashMap` car plusieurs requetes lisent la map en meme temps.

### 2. ControllerFactory.getControllerInstance (nouveau)
- `getControllerInstance(Class<?> classController, Map<Class<?>, Object> instances)`
- Lecture sans verrou : si l'instance existe, on la retourne directement.
- Sinon bloc `synchronized` + deuxieme verification (double-check) : si deux requetes arrivent en meme temps pour un controleur pas encore cree, **une seule** le cree, l'autre recupere le meme objet.

### 3. MethodExecutor
- `execute(Method method, Object instance, HttpServletRequest req, Map<String, SessionMap> sessions)`
- Recoit l'instance en parametre, ne fait plus `newInstance()`.

### 4. ProcessRequest (FrontController)
- Recupere `"controllerInstances"` dans `init()`.
- Appelle `ControllerFactory.getControllerInstance(method.getDeclaringClass(), controllerInstances)` puis passe l'instance a `MethodExecutor.execute`.

---

## Fusion avec main (conflit dans ProcessRequest.java)

`main` a recu le sprint 10-bis (gestion des roles). Les deux branches modifiaient `ProcessRequest.java`.

Resolution : on garde le code de sprint11 (instances partagees) **adapte** a la nouvelle gestion des roles de main, sinon ca ne compile pas :
- `AuthChecker.estAutorise(...)` n'existe plus dans main → remplace par `findAuth` + `estConnecte` + `hasAnyRole`.
- Ajout de `roleKey` (lu dans `init()`) et de la methode `refuser(...)` (401 non connecte / 403 role non autorise).

---

## Attention

Un controleur est maintenant **partage par toutes les requetes et tous les utilisateurs** :
- ne pas stocker d'etat propre a une requete dans ses champs (utilisateur courant, formulaire...) ;
- passer ces donnees en parametres de methode ou dans la `SessionMap`.

# TODO
- [x] map globale `ConcurrentHashMap` initialisee au demarrage (AppInitializer)
- [x] methode `getControllerInstance`
- [x] creation synchronisee (pas de doublons en cas de requetes simultanees)
- [x] MethodExecutor recoit l'instance au lieu de la recreer
- [x] FrontController utilise ce mecanisme
- [x] fusion avec main (roles du sprint 10-bis)
