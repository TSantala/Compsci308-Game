package TimoGame;

import jgame.*;
import jgame.platform.*;

public class StarWarsGameMain extends JGEngine {

	private final int alignTextLeft = -1;
	private final int maxPlayerLives = 50;
	private final int playerCollide = 1;
	private final int playerBulletID = 2;
	private final int enemyCollisionID = 3;
	private final int enemyBulletID = 4;
	private final int powerUpCollisionID = 5;
	private final int bossCollisionID = 6;
	private final int playerSize = 50;
	private final int tempInvulnTime = 50;
	private final int maxTimeNextEnemy = 100;
	private final int maxEnemyFireDelay = 80;
	private final int playerMoveSpeed = 7;
	private final int bulletMoveSpeed = 10;
	private final int maxEnemyMoveSpeed = 2;
	private final int playerStartLives = 5;
	private final int fastLevelTwoSpeed = 15;
	private final int slowLevelTwoSpeed = 7;
	private final int scoreToProgressLevel = 7500;

	private final int timeForDeathStarApproach = 300;
	private final int levelTwoBckgMoveSpeed = 12;
	private final int timeToSeeIfShotSuccessful = 100;
	private final int bossMaxLife = 600;
	private final int maxBossFireDelay = 100;

	private int timer = 0;
	private int timeNextEnemy = 1 + (int) ((int) maxTimeNextEnemy*Math.random());
	private int playerLives = playerStartLives;
	private int playerScore = 0;
	private int playerCollisionID = playerCollide;
	private int numberOfEnemiesCreated;
	private int currentEnemyCount = 0;
	private int levelTwoPlayerMoveSpeed = fastLevelTwoSpeed;
	private boolean powerUpOn = false;
	private boolean oneShot = true;

	public static void main(String[]args) {
		int windowSizeX = 800;
		int windowSizeY = 600;
		new StarWarsGameMain(new JGPoint(windowSizeX,windowSizeY));
	}

	public StarWarsGameMain(JGPoint size) {
		initEngine(size.x, size.y);
	}

	public StarWarsGameMain() {
		initEngineApplet();
	}

	private final int canvasNumXTiles = 40;
	private final int canvasNumYTiles = 30;
	private final int canvasXsize = 20;
	private final int canvasYsize = 20;

	public void initCanvas() {
		setCanvasSettings(canvasNumXTiles,canvasNumYTiles,canvasXsize,canvasYsize,null,null,null);
	}

	private final int framerate = 50;
	private final int frameskip = 2;

	public void initGame() {

		defineAllImages();
		setFrameRate(framerate,frameskip);
		setGameState("TitleScreen");

	}

	private void defineAllImages(){

		defineImage("TitleBackground","-",0,"Images/DeathStar.jpg","-");
		defineImage("StarWarsLogo","-",0,"Images/StarWarsLogoSMALL.png","-");
		defineImage("StarfieldBackground","-",0,"Images/StarfieldLARGE.png","-");

		defineImage("XWingFighter","-",0,"Images/XWingSMALL.png","-");
		defineImage("XWingFighterLARGE","-",0,"Images/XWing.png","-");
		defineImage("XWingAfterHit","-",0,"Images/XWingAfterHit.png","-");
		defineImage("XWingBullet","-",0,"Images/XWingBullet.png","-");

		defineImage("EnemyBulletRED","-",0,"Images/EnemyBulletRED.png","-");

		defineImage("PowerUp","-",0,"Images/PowerUp.png","-");

		defineImage("TieFighterLEFT","-",0,"Images/TieFighterLEFT.png","-");
		defineImage("TieFighterRIGHT","-",0,"Images/TieFighterRIGHT.png","-");
		defineImage("TieBomberLEFT","-",0,"Images/TieBomberLEFT.png","-");
		defineImage("TieBomberRIGHT","-",0,"Images/TieBomberRIGHT.png","-");
		defineImage("FinalBossLEFT","-",0,"Images/StarDestroyerLEFT.png","-");
		defineImage("FinalBossRIGHT","-",0,"Images/StarDestroyerRIGHT.png","-");

		defineImage("DeathStarShaft","-",0,"Images/DeathStarShaft.png","-");
	}




	public void startTitleScreen() {
		timer = 0;
		playerScore = 0;
		powerUpOn = false;
		oneShot = true;
		numberOfEnemiesCreated = 0;
		removeObjects(null,0);
	}

	private final int textLineOffset = 40;
	private final int smallOffset = 10;

	public void paintFrameTitleScreen() {
		drawImage(0,0,"TitleBackground");
		drawImage(0,0,"StarWarsLogo");
		drawString("Press space to begin your Assault on the Death Star!", pfWidth()/2,pfHeight()/2,0);
		drawString("Press enter to submit cheatcodes.", pfWidth()/2,pfHeight()/2+textLineOffset,0);
		drawString("Move with arrow keys, fire with space.", pfWidth()/2,pfHeight()/2+2*textLineOffset,0);
		drawString("Timo Santala",2*textLineOffset,pfHeight()-textLineOffset,0);
	}

	public void doFrameTitleScreen() {
		if(getKey(' ')) {
			clearKey(' ');
			setGameState("LevelOne");
		}
		if(getKey(KeyEnter)) {
			clearKey(KeyEnter);
			clearLastKey();
			setGameState("CheatCodeEntry");
		}
	}




	private String cheatCodeCurrentEntry = "";
	private final int cheatCodeLength = 7;

	public void paintFrameCheatCodeEntry(){
		drawImage(0,0,"TitleBackground");
		drawString("You have typed: "+cheatCodeCurrentEntry, pfWidth()/2-2*textLineOffset, textLineOffset, alignTextLeft);
		drawString("Starting Lives = "+playerLives, pfWidth()/2, pfHeight()/2-textLineOffset, 0);
		drawString("Invulnerability = "+(playerCollisionID == 0 ? "ON" : "OFF"),
				pfWidth()/2, pfHeight()/2, 0);
		drawString("Slow-Motion for skill-shot level? "+(levelTwoPlayerMoveSpeed == 15 ? "NO" : "YES"),
				pfWidth()/2, pfHeight()/2+textLineOffset, 0);
		drawString("Press enter to return to title screen.", pfWidth()/2, pfHeight()-textLineOffset,0);
	}

	public void doFrameCheatCodeEntry(){
		if(getKey(KeyEnter)) {
			clearKey(KeyEnter);
			setGameState("TitleScreen");
		}
		else if(getLastKey() != 0){
			cheatCodeCurrentEntry += getKeyDesc(getLastKey());
			clearLastKey();
			CheckAndImplementCheatCode();
		}
	}

	private void CheckAndImplementCheatCode(){
		if(cheatCodeCurrentEntry.length()>=cheatCodeLength){
			if(cheatCodeCurrentEntry.equals("ONELIFE"))
				playerLives++;
			if(cheatCodeCurrentEntry.equals("TENLIFE"))
				playerLives+=10;
			if(cheatCodeCurrentEntry.equals("DEFENSE"))
				playerCollisionID = 1 - playerCollisionID;
			if(cheatCodeCurrentEntry.equals("SLOW-MO"))
				levelTwoPlayerMoveSpeed = (levelTwoPlayerMoveSpeed == fastLevelTwoSpeed ? slowLevelTwoSpeed : fastLevelTwoSpeed);

			cheatCodeCurrentEntry = "";
		}
		if(playerLives > maxPlayerLives)
			playerLives = maxPlayerLives;
	}





	private final int backgroundMoveSpeed = 5;
	private final int backgroundStartYOffset = -1050;
	private JGObject starBackground;

	public void startLevelOne() {
		starBackground = new JGObject("aaaStarBckg",true,0,backgroundStartYOffset,0,
				"StarfieldBackground",0,backgroundMoveSpeed,JGObject.expire_off_pf);
		new PlayerXWing(pfWidth()/2,pfHeight()/2);
	}

	public void paintFrameLevelOne() {
		drawScoreAndLivesHeader();
	}

	public void doFrameLevelOne() {

		currentEnemyCount = countObjects("tiebomber",0);
		currentEnemyCount += countObjects("tiefighter",0);

		if (getKey(KeyEsc)) {
			clearKey(KeyEsc);
			playerLives = playerStartLives;
			setGameState("TitleScreen");
		}

		if(playerScore >= scoreToProgressLevel)
			setGameState("LevelTwo");

		moveObjects(null,0);

		checkCollision(enemyBulletID,playerCollisionID);
		checkCollision(enemyCollisionID, playerCollisionID);
		checkCollision(playerBulletID, enemyCollisionID);		

		if(starBackground.y >= 0){
			starBackground = new JGObject("aaaStarBckg",true,0,backgroundStartYOffset,0,
					"StarfieldBackground",0,backgroundMoveSpeed,JGObject.expire_off_pf);
		}

		timer++;
		if(currentEnemyCount < 15){
			GenerateEnemiesAtRandom();
		}

		if(numberOfEnemiesCreated >= 10){
			if(!powerUpOn && playerCollisionID != 0){
				new PowerUp();
			}
			numberOfEnemiesCreated = 0;
		}

		if(playerLives <= 0){
			setGameState("GameOver");
		}
	}

	private void GenerateEnemiesAtRandom(){

		if(timer == timeNextEnemy){

			timer = 0;
			timeNextEnemy = (int) ((int) maxTimeNextEnemy*Math.random());
			timeNextEnemy++;

			int oneOrTwo = (int) ((int) 2*Math.random());
			if(oneOrTwo == 1){
				EnemyTieFighter newEnemy = new EnemyTieFighter();
				newEnemy.fire();
			}
			else{
				EnemyTieBomber newEnemy = new EnemyTieBomber();
				newEnemy.fire();
			}
			numberOfEnemiesCreated++;
		}
	}





	public void startLevelTwo(){
		timer = 0;
		playerScore += 2500;
		removeObjects(null,0);
		starBackground = new JGObject("aaaStarBckg",true,0,backgroundStartYOffset,0,
				"StarfieldBackground",0,levelTwoBckgMoveSpeed,JGObject.expire_off_pf);
		new OneShotXWing();
		new JGTimer(timeForDeathStarApproach, true){
			public void alarm() {
				new DeathStarSurface();
				new LevelTwoTarget();
			}
		};
	}

	public void paintFrameLevelTwo(){
		drawScoreAndLivesHeader();
		drawString("You are approaching the Death Star! You have one shot left to destroy it... aim carefully!",
				pfWidth()/2,pfHeight()/2,0);
	}

	public void doFrameLevelTwo(){
		timer++;
		moveObjects(null,0);
		checkCollision(playerBulletID,enemyCollisionID);

		if(starBackground.y >= 0){
			starBackground = new JGObject("aaaStarBckg",true,0,backgroundStartYOffset,0,
					"StarfieldBackground",0,levelTwoBckgMoveSpeed,JGObject.expire_off_pf);
		}
		if(!oneShot){
			new JGTimer(timeToSeeIfShotSuccessful, true){
				public void alarm(){
					if(countObjects("1hiddentarget",0) != 0)
						setGameState("GameOver");
				}
			};
		}
	}




	public void startFinalBoss(){
		powerUpOn = false;
		removeObjects(null,0);
		new PlayerXWing(pfWidth()/2,pfHeight()/2);
		FinalBoss boss = new FinalBoss();
		boss.fire();
	}

	public void paintFrameFinalBoss(){
		drawScoreAndLivesHeader();
	}

	public void doFrameFinalBoss(){
		moveObjects(null,0);

		if(playerLives <= 0){
			setGameState("GameOver");
		}
		
		checkCollision(playerBulletID, enemyCollisionID);
		checkCollision(enemyBulletID, playerCollisionID);
	}





	public void startWinner(){
		removeObjects(null,0);
	}

	public void paintFrameWinner(){
		drawString("You have won!", pfWidth()/2, pfHeight()/2,0);
		drawString("Press enter to return to main menu.", pfWidth()/2, pfHeight()/2 + textLineOffset,0);
	}

	public void doFrameWinner(){
		if(getKey(KeyEnter)){
			clearKey(KeyEnter);
			playerLives = playerStartLives;
			setGameState("TitleScreen");
		}
	}



	public void paintFrameGameOver() {
		drawString("Your Score = "+playerScore, pfWidth()/2, pfHeight()/2 - textLineOffset, 0);
		drawString("GAME OVER", pfWidth()/2, pfHeight()/2, 0);
		drawString("Press shift to return to main menu.", pfWidth()/2, pfHeight()/2 + textLineOffset, 0);
	}

	public void doFrameGameOver() {
		if (getKey(KeyShift)){
			clearKey(KeyShift);
			playerLives = playerStartLives;
			setGameState("TitleScreen");
		}
	}








	private final int offsetToCenterBullet = 20;

	public class PlayerXWing extends JGObject {

		public PlayerXWing(double x,double y) {
			super("mainplayer",true,x,y,playerCollisionID,"XWingFighter");
		}

		public void move() {
			if(getKey(KeyUp))
				yspeed = -playerMoveSpeed;
			else if(getKey(KeyDown))
				yspeed = playerMoveSpeed;
			else
				yspeed = 0;

			if(getKey(KeyLeft))
				xspeed = -playerMoveSpeed;
			else if(getKey(KeyRight))
				xspeed = playerMoveSpeed;
			else
				xspeed = 0;

			keepInFrame(this);

			if(getKey(' ')){
				clearKey(' ');
				new XWingBullet(x+offsetToCenterBullet,y-offsetToCenterBullet);
				if(powerUpOn){
					new XWingBullet(x,y-offsetToCenterBullet);
					new XWingBullet(x+2*offsetToCenterBullet,y-offsetToCenterBullet);
				}
			}
		}

		public void hit(JGObject objectCollidedWith) {
			if(objectCollidedWith.colid == powerUpCollisionID){
				objectCollidedWith.remove();
				powerUpOn = true;
			}
			else if(objectCollidedWith.colid != playerBulletID){
				if(objectCollidedWith.colid != bossCollisionID)
					objectCollidedWith.remove();
				powerUpOn = false;
				playerLives--;
				final JGObject mainXWing = this;
				mainXWing.setGraphic("XWingAfterHit");
				mainXWing.colid = 0;
				new JGTimer(tempInvulnTime, true){
					public void alarm() {
						mainXWing.setGraphic("XWingFighter");
						mainXWing.colid = playerCollisionID;
					}
				};
			}
		}

	}

	public class XWingBullet extends JGObject {

		public XWingBullet(double x, double y){
			super("bullet",true,x,y,playerBulletID,"XWingBullet");
		}

		public void move(){
			yspeed = -bulletMoveSpeed;

			if(x > pfWidth() || x < 0 || y > pfHeight() || y < 0)
				this.remove();
		}

	}

	private final int halfTieWidth = 20;
	private final int halfTieHeight = 10;
	private final int tieValue = 100;

	public class EnemyTieFighter extends JGObject {

		public EnemyTieFighter(){
			super("tiefighter",true,(pfWidth()-playerSize)*Math.random(),textLineOffset,enemyCollisionID,
					((int) ((int) 2*Math.random()) == 1 ? "TieFighterLEFT" : "TieFighterRIGHT"));
			xspeed = maxEnemyMoveSpeed-2*maxEnemyMoveSpeed*Math.random();
			yspeed = maxEnemyMoveSpeed*Math.random();
		}

		public void hit(JGObject objectCollidedWith) {
			if(objectCollidedWith.colid == playerBulletID){
				objectCollidedWith.remove();
				this.remove();
				playerScore += tieValue;
			}
		}

		public void move(){
			if(y > pfHeight()-playerSize)
				this.remove();
			keepInFrame(this);
		}

		public void fire(){
			final EnemyTieFighter currentTieFighter = this;
			int delay = (int) ((int) maxEnemyFireDelay*Math.random());
			new JGTimer(delay, true){
				public void alarm() {
					if(!currentTieFighter.isAlive()) return;
					new EnemyBullet(currentTieFighter.x + halfTieWidth, currentTieFighter.y + halfTieHeight);
					xspeed = maxEnemyMoveSpeed-2*maxEnemyMoveSpeed*Math.random();
					currentTieFighter.fire();
				}
			};
		}

	}

	private final int bomberWidthLeft = 30;
	private final int bomberWidthRight = 60;
	private final int bomberHeight = 20;
	private final int bomberValue = 150;

	public class EnemyTieBomber extends JGObject {

		public EnemyTieBomber(){
			super("tiebomber",true,(pfWidth()-playerSize)*Math.random(),textLineOffset,enemyCollisionID,
					((int) ((int) 2*Math.random()) == 1 ? "TieBomberLEFT" : "TieBomberRIGHT"));
			xspeed = maxEnemyMoveSpeed-2*maxEnemyMoveSpeed*Math.random();
			yspeed = maxEnemyMoveSpeed-2*maxEnemyMoveSpeed*Math.random();
		}

		public void move(){
			keepInFrame(this);
		}

		public void hit(JGObject objectCollidedWith) {
			if(objectCollidedWith.colid == playerBulletID){
				objectCollidedWith.remove();
				this.remove();
				playerScore += bomberValue;
			}
		}

		public void fire(){
			final EnemyTieBomber currentTieBomber = this;
			int delay = (int) ((int) maxEnemyFireDelay*Math.random());
			new JGTimer(delay, true){
				public void alarm() {
					if(!currentTieBomber.isAlive()) return;
					new EnemyBullet(currentTieBomber.x + bomberWidthLeft, currentTieBomber.y + bomberHeight);
					new EnemyBullet(currentTieBomber.x + bomberWidthRight, currentTieBomber.y + bomberHeight);
					xspeed = maxEnemyMoveSpeed-2*maxEnemyMoveSpeed*Math.random();
					yspeed = maxEnemyMoveSpeed-2*maxEnemyMoveSpeed*Math.random();
					currentTieBomber.fire();
				}
			};
		}
	}

	public class EnemyBullet extends JGObject {

		public EnemyBullet(double x, double y){
			super("enemybullet",true,x,y,enemyBulletID,"EnemyBulletRED");
		}

		public void move(){
			yspeed = bulletMoveSpeed;
		}

	}

	private final int largeXWingSize = 180;

	public class OneShotXWing extends JGObject {

		public OneShotXWing(){
			super("xwingoneshot",true,pfWidth()/2,pfHeight()/2+largeXWingSize,playerCollisionID,"XWingFighterLARGE");
			xspeed = levelTwoPlayerMoveSpeed;
		}

		public void move(){
			if(x < 0)
				xspeed = levelTwoPlayerMoveSpeed;
			if(x > pfWidth() - largeXWingSize)
				xspeed = -levelTwoPlayerMoveSpeed;

			if(getKey(' ') && timer > 50){
				clearKey(' ');
				this.fire();
			}
		}

		public void fire(){
			if(oneShot){
				oneShot = false;
				new XWingBullet(x+largeXWingSize/2,y-2*offsetToCenterBullet);
			}
		}

	}

	public void keepInFrame(JGObject obj){
		if(obj.x < 0)
			obj.x = 0;
		if(obj.y < 0)
			obj.y = 0;
		if(obj.x > pfWidth()-playerSize)
			obj.x = pfWidth()-playerSize;
		if(obj.y > pfHeight()-playerSize)
			obj.y = pfHeight()-playerSize;
	}

	public class PowerUp extends JGObject {

		public PowerUp(){
			super("powerup",true,(pfWidth()-playerSize)*Math.random(),textLineOffset,powerUpCollisionID,
					"PowerUp");
			yspeed = maxEnemyMoveSpeed;
		}

	}

	public class DeathStarSurface extends JGObject {

		public DeathStarSurface(){
			super("backShaft",true,0,-1000,0,"DeathStarShaft");
			yspeed = backgroundMoveSpeed;
		}

		public void move(){
			if(y >= 0)
				yspeed = 0;
		}

	}

	private final int hiddenTargetXOffset = 460;
	private final int hiddenTargetYOffset = 120;

	public class LevelTwoTarget extends JGObject{

		public LevelTwoTarget(){
			super("1hiddentarget",true,hiddenTargetXOffset,hiddenTargetYOffset,enemyCollisionID,"XWingFighter");
		}

		public void hit(JGObject objectCollidedWith){
			setGameState("FinalBoss");
		}
	}

	private final int bossSpeed = 2;
	private final int finalBossWidth = 270;
	private final int finalBossHeight = 200;
	private final int hitBossPts = 10;
	private final int delayForBulletImage = 10;

	public class FinalBoss extends JGObject{
		private int life;
		public FinalBoss(){
			super("finalbossleft",true,pfWidth()/2,0,bossCollisionID,"FinalBossLEFT");
			xspeed = -bossSpeed;
			life = bossMaxLife;
		}
		public void move(){
			if(x < 0){
				x = 0;
				xspeed = 0;
				yspeed = bossSpeed;
				this.setGraphic("FinalBossRIGHT");
			}
			if(x > pfWidth() - finalBossWidth){
				x = pfWidth() - finalBossWidth;
				xspeed = 0;
				yspeed = -bossSpeed;
				this.setGraphic("FinalBossLEFT");
			}
			if(y < 0){
				y = 0;
				xspeed = -bossSpeed;
				yspeed = 0;
			}
			if(y > pfHeight() - finalBossHeight){
				y = pfHeight() - finalBossHeight;
				xspeed = bossSpeed;
				yspeed = 0;
			}
			if(life<=0){
				setGameState("Winner");
			}
		}

		public void hit(final JGObject objectCollidedWith) {
			if(objectCollidedWith.colid == playerBulletID){
				life--;
				playerScore += hitBossPts;
				new JGTimer(delayForBulletImage, true){
					public void alarm() {
						objectCollidedWith.remove();
					}
				};
			}
		}

		public void fire(){
			final FinalBoss boss = this;
			int delay = (int) ((int) maxBossFireDelay*Math.random());
			new JGTimer(delay, true){
				public void alarm() {
					if(!boss.isAlive()) return;
					createBulletsInAllDirections(boss.x+finalBossWidth/2,boss.y+finalBossHeight/2);
					boss.fire();
				}
			};
		}

	}
	
	private final int maxBulletSprayVel = 4;
	
	public void createBulletsInAllDirections(double x, double y){
		for(int xvel = -maxBulletSprayVel; xvel <= maxBulletSprayVel; xvel+=2){
			for(int yvel = -maxBulletSprayVel; yvel <= maxBulletSprayVel; yvel+=2){
				if(!(xvel==0 && yvel==0))
					new BossBullet(x,y,xvel,yvel);
			}
		}
	}
	
	public class BossBullet extends JGObject {

		public BossBullet(double x, double y, double xvel, double yvel){
			super("theenemybullet",true,x,y,enemyBulletID,"EnemyBulletRED",xvel,yvel,JGObject.expire_off_pf);
		}

	}

	public void drawScoreAndLivesHeader(){
		drawString("Score = "+playerScore,smallOffset,smallOffset,alignTextLeft);
		drawString("Lives = "+playerLives,pfWidth()-textLineOffset-smallOffset,smallOffset,0);
	}


}



