# osbot-javafx
An example osbot script which supports a JavaFX user interface for script setup

![javafx osbot user interface](https://i.gyazo.com/9856d5357e4ea893b2f951f4247f075d.png)

<b>Required dependencies</b>
- [Lombok 1.18.10](https://search.maven.org/artifact/org.projectlombok/lombok/1.18.10/jar) remember to enable Annotation Processing in InteliJ IDEA
- [OsBot 2.5.50+](https://osbot.org/)

This repo has two modules:
- fxutil : utility classes for downloading the .fxml javafx ui-sheet
- fxtest : the osbot script which `extends Script` 

<b>Implementing</b>

Configure the user interface:
- with a download URL for the source (suggested host on dropbox)
- name of the local file of the .fxml to be stored in osbot/data/
- the class for the FXML controller
- A `org.shadowrs.osbot.fxutil.Feedback` implementation for output messages

````
    public final UI         ui = new UI("https://www.dropbox.com/s/z7pash8a31l9hyf/shadowrs.reagro.testmain.fxml?dl=1",
            "/shadowrs.reagro.testmain.fxml",
            MainFxTestController.class, o -> FxLoadTest.INSTANCE.println(o));
            
````

The controller must implement `org.shadowrs.osbot.fxutil.OsbotController`

Open the UI and wait for it to be closed in the osbot script onLoop:

````
    @Override
    public int onLoop() {
        if (!ui.open() && !ui.confirmed) {
            println("starting UI");
            ui.runFX(INSTANCE);
            return 1_000;
        }
        if (!ui.confirmed) {
            println("awaiting UI closure");
            return 1_000;
        }
        // main script code
        return 1_000;
    }
````


![javafx osbot user interface](https://i.imgur.com/wS3cxz1.jpg)


# <b>FAQ</b>

Q: Will javafx work with osbot's SDN?
A: no, they've disallowed javafx so this is only for privately distributed scripts

Q: Why's it so complicated?
A: the javafx scene is actually embedded in a Swing JFrame

Q: How can I easily create and build a .fxml form?
A: [SceneBuilder 8 (for Java8)](https://gluonhq.com/products/scene-builder/#download)

Q: InteliJ embedded SceneBuilder has issues with dragging components
A: Everything must be Java 8, the project, module, build, scene builder and [InteliJ Runtime. You can choose the IDEA runtime with this plugin](https://plugins.jetbrains.com/plugin/12836-choose-runtime)
