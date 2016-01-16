package java.game;

import java.io.*;
import java.util.*;
import java.util.resource.*;
import java.render.*;	//Text
import java.render.osd.*;	//Text
import java.render.osd.dialog.*;	//Text
import java.sound.*;

import parts:part.root.BodyPart.*;




//Track kibovitese rendorrel, botokkal
public class City extends Track
{
	final static float		SPEED_LIMIT		= 18.0;	 // 18m/s = 66km/h
	final static float		SPEED_LIMIT_SQ	= SPEED_LIMIT*SPEED_LIMIT;

	final static float		TINY_SPEED_SQ	= 9.0;

	final static int		GRI_POLICECAR = cars.misc.Police:0x0006r;
	final static GameRef	GRT_POLICECAR = new GameRef( GRI_POLICECAR );

	final static int		RID_BUNTESS = frontend:0x0094r;
	final static int		RID_NRSTARTBG = frontend:0x00b6r;
	final static int		RID_NRFIN_WIN = frontend:0x00a7r;
	final static int		RID_NRFIN_LOSE = frontend:0x00c9r;
	final static int		RID_NRFIN_AI = frontend:0x00ccr;
	final static int		RID_SPEECH3 = sound:0x001dr;
	final static int		RID_SPEECH2 = sound:0x001cr;
	final static int		RID_SPEECH1 = sound:0x001br;
	final static int		RID_SPEECHGO = sound:0x001er;
	final static int		RID_SPEECHYOUWIN = sound:0x001fr;
	final static int		RID_APPLAUSE1 = sound:0x0021r;
	final static int		RID_APPLAUSE2 = sound:0x0022r;
	final static int		GREEN_ARROW = frontend:0x0070r;

	final static float		ODDS_CASH_1 = 1.00;
	final static float		ODDS_CASH_2 = 0.95;
	final static float		ODDS_PINKS  = 0.92;

	final static int		RID_SFX_DAY_WIN = sound:0x0023r;
	final static int		RID_SFX_DAY_LOOSE = sound:0x0024r;

	final static int		RID_DAY_WIN = frontend:0x00CFr;
	final static int		RID_DAY_LOOSE = frontend:0x00D0r;

	final static int		RID_DAY_CHALLENGE = frontend:0x00A5r;

	final static ResourceRef	RRT_FRAME = new ResourceRef(frontend:0x00CBr);
	
	// commands, nem utkozhet a Track.java commandokkal!!
	final static int		CMD_PARTICIPATE = 1001;
	final static int		CMD_WATCH_RACE = 1002;
	final static int		CMD_STOP_WATCHING = 1003;

	final static int		NR_INVALID = -1;
	final static int		NR_IDLE = 0;
	final static int		NR_SHOWSTART = 1;
	final static int		NR_3 = 2;
	final static int		NR_2 = 3;
	final static int		NR_1 = 4;
	final static int		NR_START = 5;
	final static int		NR_RACE = 6;
	final static int		NR_FINISH = 7;
	final static int		NR_SHOWFINISH = 8;


    Vector3[]	posGarage = new Vector3[GameLogic.CLUBS];
    Ypr[]		oriGarage = new Ypr[GameLogic.CLUBS];


    int						time;
    int						prize;
	
	Text					statusTxt, oppStatusTxt;
	int						oppStatusDisplayed;
	int						collision;


    //--------day racing stuff--------:
    Vector3                 raceStart, raceFinish;
    Trigger                 trRaceFinish;
    RenderRef               finishObject;
	RaceDialog				raceDialog;

    Racer                   challenger, challenged;
    Marker                  mStart, mFinish;

    Bot						raceBot, demoBot;
    Marker                  mRaceBot;
    int						raceState; //0-nop 1-race 2-race after the winner crossed the finish line
	int						aiChallengeState;
    int						abandoned;
	int						abandoned2;

	Vector					opponentCars = new Vector();		//TrafficTracker

	//----------night race stuff---------:
	GameRef					nrcameraTarget;
	int						nightTime;
	RenderRef				nrStarterLady;
	Animation				nrStarterLadyAnim;
	int						nrStat;
	int						nrDelay = 0;
	int						nrBotID1, nrBotID2;
	Vector3					pS, pF, dirS, dirF;
	Ypr						oriS, oriF;
	Bot						nrBot1, nrBot2;
	Trigger					nrFinishTrigger;
	Trigger					nrFinishSoundTrigger;

	int						nrPlayerRace; //0-semmi; 1-varakozas; 2-van
	int						nrNear;		// kozelben vagyok, nrgroup on
	int						nrWatching;	// watching group on
	int						nrPlayerPaused;
	int						nrShowRaceStart;
	int						nrShowRaceFinish;
	int						backupnrWatching;

	int						nrPrize;
	ResourceRef[]			fakers;
	ParkingCar[]			parkingCars;
	RenderRef[]				nrMen;
	Animation[]				nrMenAnim;
	float					yawNudge = 0.2;
	float					posNudge = 0.3;
	int						nightRaceGroup;	// osd group
	int						nrWatchingGroup;
	int						nrLookAt;		// melyik bot-ot nezi? 1 vagy 2
	int						nrFinished1;	// bot1 celba ert
	int						nrFinished2;	// bot2 vagy player celba ert
	int						nrQuit;			// elvesztette a kocsit?
	float					nrStartTime;
	float					nrTime1;
	float					nrTime2;
	ResourceRef				nrHead1, nrHead2;
	String					nrName1, nrName2;
        Racer                                   pl1, pl2;
	NightracesData			nrData = new NightracesData();
	int						nrLastPlaceDay = -1;
	Vector					nrOpponents = new Vector();
	int[]					nrPrizeList;
	int						nrcameraMode_before;
	int						applauseSfxID;
	int						applauseSfxOn;

	SfxRef					applause1 = new SfxRef(RID_APPLAUSE1);
	SfxRef					applause2 = new SfxRef(RID_APPLAUSE2);
	SfxRef					speech3 = new SfxRef(RID_SPEECH3);
	SfxRef					speech2 = new SfxRef(RID_SPEECH2);
	SfxRef					speech1 = new SfxRef(RID_SPEECH1);
	SfxRef					speechGO = new SfxRef(RID_SPEECHGO);
	SfxRef					speechYOUWIN = new SfxRef(RID_SPEECHYOUWIN);
	SfxRef					speechDAYYOUWIN = new SfxRef(RID_SFX_DAY_WIN);
	SfxRef					speechDAYYOULOOSE = new SfxRef(RID_SFX_DAY_LOOSE);

    //--------police stuff---------:
	int						maxScouts = 2;	//egyszerre hany uldozhet

	Vector					policeCars = new Vector();		//TrafficTracker
	Vector					alertedScouts = new Vector();	//PoliceScout

	float					lastCollisionTime;
	float					lastAlertTime;
	float					firstAlertTime;	//az elso figy ideje, a fleedAway megallapitasahoz
	float					pullOverTime;	//mikor huzodott felre? 
    int						policeState; //0-semmi 10... buntetni akar
	int						roamFree;	//amig 1, nem buntethet meg (buntetes utan)

	//buntetes osszesito:
	float					overSpeed;	//gyorshajtasert
	int						crashes;		//mas kocsik osszetoreseert
	int						fleedAway;		//elmenekult-e, vagy megallt a felszolitasra?

	int						backupCam;

	MultiplayerSocket 		MP;

	String socket = "aabbccddeeffggg";

//----------------------------------------------------------------------

	public void prepareNightRace()
	{
		int nrPlace;

		if( nrLastPlaceDay != GameLogic.day )
		{
			nrLastPlaceDay = GameLogic.day;
//			nrPlace = Math.random() * nrData.startPos.length;
			nrPlace = Math.random()*5 + GameLogic.player.club*6;
		}

		if( 1 )
		{
			pS = nrData.startPos[ nrPlace ];
			pF = nrData.finishPos[ nrPlace ];
			oriS = nrData.startOri[ nrPlace ];
			oriF = nrData.finishOri[ nrPlace ];
			dirS = new Vector3( oriS );
			dirF = new Vector3( oriF );
		}
		else
		{
			// csak tesztelesre!!
			pS = map.getNearestCross( posStart );
			pF = map.getNearestCross( pS, 150 );
			dirS = map.getStartDirection( pS, pF );	//normalized!
			dirF = map.getStartDirection( pF, pS );	//normalized!
			oriS = new Ypr( dirS );
			oriF = new Ypr( dirF );
			Vector3 tmp = new Vector3( dirS );
			tmp.mul( 20.0 );
			pS.add( tmp );	//keresztezodesbe ne logjanak be!
			tmp = new Vector3( dirF );
			tmp.mul( 20.0 );
			pF.add( tmp );	//keresztezodesbe ne logjanak be!
		}

		map.haltTrafficPath( pS, pF );

		nrNear = 0;
		nrWatching = 0;
		nrPlayerPaused = 0;
		nrPlayerRace = 0;
		nrShowRaceStart = 0;
		nrShowRaceFinish = 0;
	}

    public void enter( GameState prev_state )
    {
		MP = new MultiplayerSocket();

        if( prev_state instanceof RaceSetup)
        {
			Frontend.loadingScreen.show();
			osd.show();
 		} 
		else
        {
			//benne vagyunk, ne dobjon vissza azonnal
			activeTrigger=-1;

            abandoned=raceState=0;
			aiChallengeState=0;
            policeState=0;
			roamFree=0;

			overSpeed=0;
			crashes=0;
			fleedAway=0;

			nightTime = 0;

			if( GameLogic.gameMode == GameLogic.GM_FREERIDE || GameLogic.gameMode == GameLogic.GM_SINGLECAR )
			{
//				changeCamTarget(player.car);
//				changeCamFollow();
			} 
			else
			if( GameLogic.gameMode == GameLogic.GM_QUICKRACE )
			{
				createQuickRaceBot();
				changeCamTarget2(raceBot.car);
			} 
			else
			if( GameLogic.gameMode == GameLogic.GM_DEMO )
			{
				osdEnabled = 0;
				enableOsd( osdEnabled );

				if (1)
				{
					if (player.car)
					{
						player.car.destroy();
						player.controller.command("leave " + player.car.id());
						player.car = null;
					}

					raceBot = new Bot( Math.random()*59, Math.random()*1234, Math.random(), 2.0, 2.0, 1.0);
					demoBot = new Bot( Math.random()*59, Math.random()*1234, Math.random(), 2.0, 2.0, 1.0);
					raceBot.command("osd 0");
					demoBot.command("osd 0");

//					raceBot.createCar( map, GameLogic.carSaveDir + "ishuma1" );
//					demoBot.createCar( map, GameLogic.carSaveDir + "einwagen" );

					VehicleDescriptor vd;
					vd = GameLogic.getVehicleDescriptor( VehicleType.VS_DEMO );
					raceBot.createCar( map, new Vehicle( map, vd.id, vd.colorIndex, vd.optical, vd.power, vd.wear, vd.tear ));
					vd = GameLogic.getVehicleDescriptor( VehicleType.VS_DEMO );
					demoBot.createCar( map, new Vehicle( map, vd.id, vd.colorIndex, vd.optical, vd.power, vd.wear, vd.tear ));

					if (raceBot.car)
					{
						raceBot.car.setParent( map );
						raceBot.car.setMatrix( posStart, oriStart );
						raceBot.car.command( "reset" );
						raceBot.car.command( "reload" );	//Fuel and NOS
					}
					if (demoBot.car)
					{
						demoBot.car.setParent( map );
						demoBot.car.setMatrix( posStart, oriStart );
						demoBot.car.command( "reset" );
						demoBot.car.command( "reload" );	//Fuel and NOS
					}

					player.car = demoBot.car;	//patch
/*				} else
				{
					createQuickRaceBot();
					player.controller.command("leave " + player.car.id());
					demoBot = new Bot( player.character.id(), Math.random()*1234, Math.random(), 2.0, 2.0, 1.0);
					demoBot.createCar( map, new Vehicle(player.car) );
*/				}
				changeCamTarget(demoBot.car);
				changeCamTarget2(raceBot.car);	
			} 
			else
			{
				float hour = GameLogic.getTime() / 3600;
				if( hour < 4 || hour > 22 )
				{	//night race time!
					nightTime = 1;

					nrStat = NR_IDLE;
					fakers = new ResourceRef( cars:0x0027r ).getChildNodes();

					parkingCars = new ParkingCar[20];
					int np = 0;

					nrMen = new RenderRef[20];
					nrMenAnim = new Animation[20];
					int nm = 0;

					float halfStreetWidth = 8;
					float parkingYaw = 0.7;
					Vector3[] alignedPos;

					alignedPos = map.alignToRoad( pS );
					pS = new Vector3( alignedPos[ 0 ] );
					alignedPos = map.alignToRoad( pF );
					pF = new Vector3( alignedPos[ 0 ] );

					int[] manID = new int[6];
					manID[ 0 ] = humans:0x002fr;
					manID[ 1 ] = humans:0x0030r;
					manID[ 2 ] = humans:0x0031r;
					manID[ 3 ] = humans:0x0032r;
					manID[ 4 ] = humans:0x0033r;
					manID[ 5 ] = humans:0x0034r;

					int[] manAnimID = new int[6];
					manAnimID[ 0 ] = humans:0x0014r;
					manAnimID[ 1 ] = humans:0x001br;
					manAnimID[ 2 ] = humans:0x007dr;
					manAnimID[ 3 ] = humans:0x007er;
					manAnimID[ 4 ] = humans:0x0017r;
					manAnimID[ 5 ] = humans:0x007fr;

					Vector3 spacin, sideoffs, botPos;
					Ypr oriCars;
					RenderRef rr;
					
					//starthoz
					alignedPos = map.alignToRoad( pS );
					alignedPos[ 1 ].normalize();

					spacin = new Vector3( alignedPos[ 1 ] );
					spacin.mul( 2.5 );

					sideoffs = new Vector3( alignedPos[ 1 ] );
					sideoffs.y = 0;	//lejtos utcan gaz: egyik oldalt belenyomja, masikon levegobe logatja a cuccost
					sideoffs.mul( halfStreetWidth );
					sideoffs.rotate( new Ypr(1.57,0.0,0.0) );

					for( int i=0; i<5; i++ )
					{
						float	pNudge, yNudge;
						int		idx;
						int		colorSeed;
						int		man;

						pNudge = (Math.random()*2-1)*posNudge;
						yNudge = (Math.random()*2-1)*yawNudge;
						idx = Math.random()*fakers.length;
						colorSeed = Math.random()*12345;

						botPos = new Vector3( alignedPos[ 0 ] );
						botPos.add( sideoffs );
						oriCars = new Ypr( alignedPos[ 1 ] );
						oriCars.y -= parkingYaw + yNudge;
						parkingCars[ np++ ] = new ParkingCar( map, fakers[idx], botPos, oriCars, colorSeed );

						botPos.add( spacin );
						man = Math.random()*manID.length;
						nrMen [nm] = new RenderRef( map, manID[ man ], "tesztmen" );
						nrMenAnim[nm] = new Animation( nrMen[nm], new ResourceRef(manAnimID[ man ]) );
						nrMenAnim[nm].setSpeed( 0.5+Math.random() );
						nrMenAnim[nm].loopPlay();
						oriCars.y+=3.14;
						nrMen [nm].setMatrix( botPos, oriCars );
						oriCars.y-=3.14;
						nm++;



						pNudge = (Math.random()*2-1)*posNudge;
						yNudge = (Math.random()*2-1)*yawNudge;
						idx = Math.random()*fakers.length;
						colorSeed = Math.random()*12345;

						botPos = new Vector3( alignedPos[ 0 ] );
						botPos.sub( sideoffs );
						oriCars = new Ypr( alignedPos[ 1 ] );
						oriCars.y += parkingYaw + yNudge;
						parkingCars[ np++ ] = new ParkingCar( map, fakers[idx], botPos, oriCars, colorSeed );

						botPos.add( spacin );
						man = Math.random()*manID.length;
						nrMen [nm] = new RenderRef( map, manID[ man ], "tesztmen" );
						nrMenAnim[nm] = new Animation( nrMen[nm], new ResourceRef(manAnimID[ man ]) );
						nrMenAnim[nm].setSpeed( 0.5+Math.random() );
						nrMenAnim[nm].loopPlay();
						oriCars.y+=3.14;
						nrMen [nm].setMatrix( botPos, oriCars );
						oriCars.y-=3.14;
						nm++;

						alignedPos[ 0 ].add( spacin );
						alignedPos[ 0 ].add( spacin );
						alignedPos = map.alignToRoad( alignedPos[ 0 ] );
						alignedPos[ 1 ].normalize();
					}


					//finishbe
					alignedPos = map.alignToRoad( pF );
					alignedPos[ 1 ].normalize();

					spacin = new Vector3( alignedPos[ 1 ] );
					spacin.mul( 2.5 );

					sideoffs = new Vector3( alignedPos[ 1 ] );
					sideoffs.y = 0;	//lejtos utcan gaz: egyik oldalt belenyomja, masikon levegobe logatja a cuccost					sideoffs.mul( halfStreetWidth );
					sideoffs.mul( halfStreetWidth );
					sideoffs.rotate( new Ypr(1.57,0.0,0.0) );

					for( int i=0; i<5; i++ )
					{
						float	pNudge, yNudge;
						int		idx;
						int		colorSeed;
						int		man;

						pNudge = (Math.random()*2-1)*posNudge;
						yNudge = (Math.random()*2-1)*yawNudge;
						idx = Math.random()*fakers.length;
						colorSeed = Math.random()*12345;

						botPos = new Vector3( alignedPos[ 0 ] );
						botPos.add( sideoffs );
						oriCars = new Ypr( alignedPos[ 1 ] );
						oriCars.y -= parkingYaw + yNudge;
						parkingCars[ np++ ] = new ParkingCar( map, fakers[idx], botPos, oriCars, colorSeed );

						botPos.add( spacin );
						man = Math.random()*4;
						nrMen [nm] = new RenderRef( map, manID[ man ], "tesztmen" );
						nrMenAnim[nm] = new Animation( nrMen[nm], new ResourceRef(manAnimID[ man ]) );
						nrMenAnim[nm].setSpeed( 0.5+Math.random() );
						nrMenAnim[nm].loopPlay();
						oriCars.y+=3.14;
						nrMen [nm].setMatrix( botPos, oriCars );
						oriCars.y-=3.14;
						nm++;

						pNudge = (Math.random()*2-1)*posNudge;
						yNudge = (Math.random()*2-1)*yawNudge;
						idx = Math.random()*fakers.length;
						colorSeed = Math.random()*12345;

						botPos = new Vector3( alignedPos[ 0 ] );
						botPos.sub( sideoffs );
						oriCars = new Ypr( alignedPos[ 1 ] );
						oriCars.y += parkingYaw + yNudge;
						parkingCars[ np++ ] = new ParkingCar( map, fakers[idx], botPos, oriCars, colorSeed );

						botPos.add( spacin );
						man = Math.random()*4;
						nrMen [nm] = new RenderRef( map, manID[ man ], "tesztmen" );
						nrMenAnim[nm] = new Animation( nrMen[nm], new ResourceRef(manAnimID[ man ]) );
						nrMenAnim[nm].setSpeed( 0.5+Math.random() );
						nrMenAnim[nm].loopPlay();
						oriCars.y+=3.14;
						nrMen [nm].setMatrix( botPos, oriCars );
						oriCars.y-=3.14;
						nm++;

						alignedPos[ 0 ].add( spacin );
						alignedPos[ 0 ].add( spacin );
						alignedPos = map.alignToRoad( alignedPos[ 0 ] );
						alignedPos[ 1 ].normalize();
					}

					finishObject = new RenderRef( map, GREEN_ARROW, "finishObject" );
					finishObject.setMatrix( new Vector3( pF.x, pF.y + 3.0, pF.z ), null );

					addTrigger( pS, null, Marker.RR_START, "event_handlerNrStart", 13, "night race start trigger" );
				}
			}
		}

		new SfxRef( sound:0x0001r ).cache(); //def idle
		new SfxRef( sound:0x0002r ).cache(); //def down
		new SfxRef( sound:0x0003r ).cache(); //def up
		new SfxRef( sound:0x0016r ).precache(); //police siren

		//
        super.enter(prev_state);	//waits for loading to finish!
		//

        if( prev_state instanceof RaceSetup)
        {
 		}
        else
        {
            setEventMask( EVENT_COLLISION );
            addNotification( GameLogic.player.car, EVENT_COLLISION, EVENT_SAME, null );

			if( GameLogic.gameMode == GameLogic.GM_QUICKRACE )
			{
				changeCamTarget2(raceBot.car);
			}

			if ( GameLogic.gameMode != GameLogic.GM_DEMO && GameLogic.gameMode != GameLogic.GM_SINGLECAR )
			{
				osd.createRectangle( -0.42, -0.95, 1.2, 0.10, -1, new ResourceRef(frontend:0x0092r) );
				statusTxt = osd.createText( null, Frontend.smallFont, Text.ALIGN_LEFT, -0.98, -0.98 );
				if( GameLogic.gameMode != GameLogic.GM_FREERIDE )
				{
					osd.createRectangle( -0.47, -0.86, 1.1, 0.10, -1, new ResourceRef(frontend:0x0092r) );
					oppStatusTxt = osd.createText( null, Frontend.smallFont, Text.ALIGN_LEFT, -0.98, -0.89 );

					if( GameLogic.gameMode == GameLogic.GM_QUICKRACE )
					{
						String txt = raceBot.name;
						if (raceBot.car.chassis)
  							txt = txt + " riding a " + raceBot.car.chassis.vehicleName;
						oppStatusTxt.changeText( txt );
					}
				}
			}

			if( GameLogic.gameMode == GameLogic.GM_CARREER )
			{
				if( nightTime )
				{
					if( player.checkHint(Player.H_NIGHTCITY) )
						new WarningDialog( player.controller, Dialog.DF_MODAL|Dialog.DF_DEFAULTBG,  "Welcome to ValoCity!", "At night the racing community gathers to arrange drag races for high prizes even for pink slips! \n You can find them at the location marked with the green flag, to race or just to watch others race.").display();
				}
				else
				{
					if( player.checkHint(Player.H_DAYCITY) )
						new WarningDialog( player.controller, Dialog.DF_MODAL|Dialog.DF_DEFAULTBG, "Welcome to ValoCity!", "Find opponents from your club to race, for money prizes or just for prestige. \n Get to know them so you'll know who to race at the night races! (Night races take place around midnight) \n Beware of the cops, pulling over is often easier than trying to get away.").display();
				}

			}

			osd.endGroup();

			osd.globalHandler = this;

			Style butt0 = new Style( 0.3, 0.12, Frontend.mediumFont, Text.ALIGN_LEFT, Osd.RRT_TEST );

			Menu m;
			
			m = osd.createMenu( butt0, -1.0, -0.5, 0 );
			m.addItem( "PARTICIPATE", CMD_PARTICIPATE );
			m.addItem( "WATCH RACE", CMD_WATCH_RACE );
			osd.hideGroup( nightRaceGroup = osd.endGroup() );

			m = osd.createMenu( butt0, -1.0, -0.5, 0 );
			m.addItem( "STOP WATCHING", CMD_STOP_WATCHING );
			m.addItem( "PARTICIPATE", CMD_PARTICIPATE );
			osd.hideGroup( nrWatchingGroup = osd.endGroup() );
		}
		refreshStatus();	//ITT lehet.

    }

    public void exit( GameState next_state )
    {
        if( next_state instanceof RaceSetup)
        {
			osd.hide();
		}
        else
        {       
			int i;
            clearEventMask( EVENT_COLLISION );

			if( raceState )
				cleanupRace();

            removeAllTimers();

            destroyRaceBot();
			if (demoBot)
			{
				demoBot.deleteCar();
				demoBot=null;
			}
			//flush :o)
			if(( GameLogic.gameMode == GameLogic.GM_DEMO )
			 ||( GameLogic.gameMode == GameLogic.GM_QUICKRACE )
			 ||( GameLogic.gameMode == GameLogic.GM_FREERIDE ))
			{
				killCar = 1;
/*				if (player.car)
				{
					player.car.destroy();
					player.car = null;
				}
*/			}

	
			for( i=alertedScouts.size()-1; i>=0; i-- )
			{
				PoliceScout pc = alertedScouts.removeLastElement();
				pc.bot.deleteCar();
				nav.remMarker( pc.tracker.m );
			}

			for( i=policeCars.size()-1; i>=0; i-- )
			{
				TrafficTracker tt = policeCars.removeLastElement();
				tt.car.release();
				nav.remMarker( tt.m );
			}

			for( i=opponentCars.size()-1; i>=0; i-- )
			{
				TrafficTracker tt = opponentCars.removeLastElement();
				tt.car.release();
				nav.remMarker( tt.m );
			}

			cleanupNightRace();

			parkingCars = null;
			nrMen = null;
			fakers = null;
        }

        super.exit(next_state);
    }

	/**
	 * Fires before frame render
	 */
	public void	frame(){
		refreshStatus();
		sendPositionDatagram();
	}

	public void sendPositionDatagram() {
		if (MP) {
			if (player.car && player.car.chassis) {
				Vector3 pos = player.car.getPos();
				MP.send("POS" ,pos.x + ";" + pos.y + ";" + pos.z);
			}
		}
	}

	public void animate(){
		frame();
	}

	public void	refreshStatus()
	{
		if (statusTxt)
		if (player.car)
		{
			String txt;
			if( GameLogic.gameMode == GameLogic.GM_QUICKRACE || GameLogic.gameMode == GameLogic.GM_FREERIDE )
			{
				txt = player.name;
			}
			else
			{
				int	ranking = (GameLogic.CLUBMEMBERS-(GameLogic.findRacer(player)-GameLogic.CLUBMEMBERS*player.club));
				txt = player.name + "  " + player.club + "/" + ranking + "  $" + player.money + " >" + player.getPrestigeString();
			}

			//if (config.majomParade)
			{
				/*String s;
				File dtm = new File("trololo");
				if( dtm.open( File.MODE_READ ) )
				{
					s  = dtm.readString();
					dtm.close();
				}*/


				Vector3 carpos=player.car.getPos();
				if (player.car && player.car.chassis)
					txt = socket;
  				//txt = txt + " riding a " + player.car.chassis.vehicleName+" at "+carpos.x;
			}
			statusTxt.changeText( txt );
		}
	}

	public void alertPolice()
	{
		//setMessage( "PAlert" );

		float time = System.simTime();
		if( time-lastAlertTime < 3.0 )	return;///utkozesek gyors sorozatban is hivhatnak!

		lastAlertTime = time;
        if( roamFree ) return;

		//van ures hely, vagy lokjunk vissza valakit a forgalomba aki mar nagyon lemaradt?

		int	chasingScouts;
		
		float maxdst;
		PoliceScout pc, maxpc;
		for( int i=alertedScouts.size()-1; i>=0; i-- )
		{
			if( !(pc=alertedScouts.elementAt(i)).returningTraffic )
			{
				chasingScouts++;
				if( pc.distance > maxdst )
				{
					maxdst = pc.distance;
					maxpc = pc;
				}
			}
		}

		//police notice distance!  sync with police lost distance!
		float	distance=50.0+25*player.club;

		float	d;
		TrafficTracker theOne;
		int	j;

		for( int i=0; i<policeCars.size(); i++ )
		{
			TrafficTracker tt = policeCars.elementAt(i);
			GameRef pc = tt.car;

			if( pc.id() )
			{
				//hmm, mi van, ha eppen Denes vezetteti vissza a forgalomba? Hagyjunk neki beket!
				//(az 1 mp-s ellenorzes kesese miatt lehet ilyen!!, vagy ha egy masik forgalmi kocsi loki ki)
				//inkonzisztencia, megprobaljuk kijavitani.. (lehet, hogy meg 0)
				tt.trafficId = pc.getInfo( GII_CAR_TRAFFICPTR );

				if( tt.trafficId )
				{
					//Vector3 v = pc.getPos();
					//v.sub( player.car.getPos() );
					//d = v.length();

					//nem legvonalbeli, hanem uton mert tavolsagot hasonlitunk;
					//igy elkerulheto pl hid alatt hajto rendor riasztasa a hidrol, etc.
					d = map.getRouteLength( pc.getPos(), player.car.getPos() );

					if( d>0 && d<distance )
					{
						distance = d;
						theOne = tt;
						j=i;
					}
				}
			}
			else
			{
				//setMessage( "Pdisappeared" );
				policeCars.removeElementAt( i );
				nav.remMarker( tt.m );
			}
		}

		//van egy rendor aki uldozhetne
		if( theOne )
		{
			//setMessage( "PFound!" );
			if( distance < 80.0 )
			{
				int	killTheLamest; 

				//setMessage( "Pfound80free! " + chasingScouts );
				if( chasingScouts>=maxScouts )
				{
					if( distance*3 < maxdst )
					{
						//setMessage( "Pfound80free! " + chasingScouts + " kill" );
						killTheLamest = 1;
						chasingScouts--;
					}
				}

				if( chasingScouts<maxScouts )
				{
					//uj rendort inditunk!

					//kiszedjuk a rendelkeyesre allok listajabol, ne zavarkodjon ott
					policeCars.removeElementAt(j);
					
					PoliceScout pc = new PoliceScout();
					pc.distance = distance;
					pc.tracker = theOne;

					wakePoliceScout( pc );

					alertedScouts.addElement( pc );
				}

				if( killTheLamest )
				{
					//setMessage( "xxxxxxxxxxxxxxxxxxx" );
					sleepPoliceScoutQuick( maxpc );
				}
			}
		}
	}

    public void wakePoliceScout( PoliceScout pc )
    {
		if( !policeState )
			firstAlertTime=System.simTime();	//most kezdodik a buli!

        pc.bot = new Bot( 0, 12345, 0.0+player.club*0.25 ); //0 ... 0.75
		pc.bot.setDriverObject( GameLogic.HUMAN_POLICEMAN );
		//setMessage( "Pwake" );
        pc.bot.createCar( map, new Vehicle(pc.tracker.car) );
		pc.bot.traffic_id = pc.tracker.trafficId;		//mivel most meg a trafficben van!
		pc.bot.imaPoliceDriver=1;

		//rosszalkodunk, megprobal utolerni
		policeState = 10;
		pc.bot.followCar( player.car, 10 );
		pc.bot.pressHorn();

		//if( pc.bot.brain.id() != pc.bot.car.getInfo( GII_OWNER ) )
		//	System.exit( "grrr" );

		if( GameLogic.gameMode != GameLogic.GM_DEMO )
			changeCamTarget2(pc.bot.car);
	}

    public void sleepPoliceScout( PoliceScout pc )
    {
		//setMessage( "Pgob" );
		pc.bot.releaseHorn();

		//visszateres a forgalomba:
		//szolunk neki, hogy menjen vissza
		//a Bot eventhandlere kezeli le a visszateresi esemenyt - idokozben azonban Denes kitorolhette (random), viszateres kozben, ha nem latszott
		//ekkor a traffic_id beaalitodik, ezt eszrevesszuk itt, a handleevent/time-ban, es toroljuk a pc-t

		pc.returningTraffic = 1;
		pc.bot.addNotification( pc.bot.car, EVENT_COMMAND, EVENT_SAME, null );
		pc.bot.reJoinTraffic();
    }

    public void sleepPoliceScoutQuick( PoliceScout pc )
    {
		//setMessage( "Pgob-quick" );

		Vector3 pos = pc.bot.car.getPos();
		nav.remMarker( pc.tracker.m );
		pc.bot.releaseHorn();
		pc.bot.deleteCar();

		alertedScouts.removeElement(pc);

		map.addTrafficP( GRT_POLICECAR, pos, 1, 2, 5, 2);
    }

    public void createQuickRaceBot()
    {
		int characterIndex = Math.random()*59;
		if( characterIndex+Racer.RID_FEJ == player.character.id() )
			characterIndex++;

		raceBot = new Bot( characterIndex, Math.random()*1234, Math.random(), 2.0, 2.0, 1.0);
		raceBot.botVd = GameLogic.getVehicleDescriptor( VehicleType.VS_DEMO );

        Vector3 pos = player.car.getPos();
        Vector3 vel = player.car.getVel();
		vel.normalize();
        vel.mul(500.0f);
        pos.add(vel);
        raceBot.createCar( map );
    }


    public void destroyRaceBot()
    {
        if( raceBot )
        {
            if( mRaceBot )	//rem day bot's marker
            {
                nav.remMarker( mRaceBot );
                mRaceBot=null;
            }

            raceBot.deleteCar();
			raceBot=null;
        }
    }


    //prepare to the race:
    public void startRace( Vector3 pStart, Vector3 pFinish, int moneyprize )
    {
        raceStart=pStart; raceFinish=pFinish;
        prize = moneyprize;

        Vector3 startDir = map.getStartDirection( pStart, pFinish );
        Ypr startOri = new Ypr( startDir );

		//1, siman forgalomban volt
		//2, epp visszatartott oda (utkozes/verseny utan)

		//hide dummycar, create real racecar (at same place) instead
		if( raceBot.dummycar )
		{
			Vector3	pos = raceBot.dummycar.getPos();
			Ypr		ori = raceBot.dummycar.getOri();

			if( raceBot.brain )	//2
			{
				raceBot.brain.destroy();
				raceBot.brain = null;
				raceBot.car.release();
				raceBot.car = null;
			}
			else				//11
			{
				map.remTrafficCar( raceBot.traffic_id );
				raceBot.traffic_id = 0;
			}

			raceBot.dummycar.command( "reset" );
			raceBot.dummycar.setMatrix( new Vector3(0.0, -10000.0, 0.0), ori );
			raceBot.dummycar.setParent( raceBot );
			
			raceBot.createCar( map );
			raceBot.car.command( "reset" );
			raceBot.car.setMatrix( pos, ori );
			raceBot.car.setParent( map );
		}
		else
		{	//quickrace, pl.
		}
		
		//nehogy elteleportalja  a forgalommal egyutt
        raceBot.stop(); //barmit is csinalt (trafficben, ai-kent, stb.) hagyja abba, alljon meg
		mRaceBot = nav.addMarker( raceBot );	//add day race bot's marker

        //kitakaritjuk a keresztezodest:
        map.haltTrafficCross( raceStart, 15.0 );

        //move camera (player _IS_ participating)
        Vector3 camPos = new Vector3( startDir );
        camPos.mul( -7.0 );     //moge
        camPos.y+=3; //fole
        camPos.add( raceStart );
		if (cam)
		{
			Ypr ypr = new Ypr( startOri );
			ypr.p -= 0.3;
			cam.setMatrix( camPos, startOri );
		}

        Vector3 Pos_left = new Vector3( startDir );
        Pos_left.mul( 1.75 );              //melle
        Pos_left.rotate( new Ypr(1.57,0.0,0.0) );
        Pos_left.add( raceStart );
        Vector3 Pos_right = new Vector3( startDir );
        Pos_right.mul( 1.75 );              //melle
        Pos_right.rotate( new Ypr(-1.57,0.0,0.0) );
        Pos_right.add( raceStart );

		//sometimes exchange left-right 
		if (Math.random() > 0.5)
		{
			Vector3	tmp = Pos_left;
			Pos_left = Pos_right;
			Pos_right = tmp;
		}

        mStart = nav.addMarker( Marker.RR_START, pStart, 3 );
        mFinish = nav.addMarker( Marker.RR_FINISH, pFinish, 3 );

        //add finish trigger
        trRaceFinish = new Trigger( map, null, raceFinish, "dayrace_finish_trigger" );
        addNotification( trRaceFinish.trigger, EVENT_TRIGGER_ON, EVENT_SAME, null, "event_handlerRaceFinish" );

        finishObject = new RenderRef( map, frontend:0x00000070r, "finishObject" );
        Vector3 tmp = new Vector3( raceFinish );
        tmp.y+=3;
        finishObject.setMatrix( tmp, null );

        //set player, set opponent

		if (raceBot)
		{
			raceBot.car.command( "reset" );
			raceBot.car.setMatrix( Pos_left, startOri );
			raceBot.car.setParent( map );	//quickrace botokhoz kell
			raceBot.car.command( "stop" ); 
			raceBot.car.command( "idle" );
			raceBot.brain.command( "AI_BeginRace 0.5" );
		}

		if (demoBot)
		{
			demoBot.car.command( "reset" );
			demoBot.car.setMatrix( Pos_right, startOri );
			demoBot.car.setParent( map );	//quickrace botokhoz kell
			demoBot.car.command( "stop" ); 
			demoBot.car.command( "idle" );
			demoBot.brain.command( "AI_BeginRace 0.5" );
		} else
		if (player.car)
		{
	        player.car.command( "reset" );
		    player.car.setMatrix( Pos_right, startOri );
			player.car.command( "stop" ); 
			player.car.command( "idle" );
		}

        setEventMask( EVENT_TIME ); //meg a city.enter() elott vagyunk

        //ha valaki beallitotta volna (pl ai csinal ilyet temp), visszaallitjuk!
        abandoned=0;

        addTimer( 1, 9 );
    }

    //visszaszamlalas vege, start:
    public void startRace2()
    {
		if (player.car)
		{
			player.car.setCruiseControl(0);
			player.car.command( "start" ); 
		}
		if (raceBot)
		{
			if (demoBot)
			{
				raceBot.car.command( "start" ); 
				demoBot.car.command( "start" ); 
				raceBot.startRace( raceFinish, demoBot );
				demoBot.startRace( raceFinish, raceBot );
			} else
			{
				raceBot.car.command( "start" ); 
				raceBot.startRace( raceFinish, player );
			}
		}

        if(	GameLogic.klampiPatch >= 2)
        {
	        if(	GameLogic.klampiPatch >= 3)
			{
				player.controller.command( "viewport 0" );
				player.controller.command( "osd 0" );
			}

            raceBot.brain.command( "camera " + cam.id() );

	        if(	GameLogic.klampiPatch >= 3)
			{
				if ( osdEnabled )
					raceBot.brain.command( "osd " + osd.id() );
				raceBot.brain.command( "viewport " + osd.getViewport().id() );
			}
        }

        Sound.changeMusicSet( Sound.MUSIC_SET_RACE );
    }

	public void lookBot( Bot bot, int init )
	{
		if( init )
		{
			changeCamNone();
		}

		if( bot )
		{
			changeCamTarget(bot.car);
//			changeCamFollow();
			changeCamChase();
		} 
		else
		{//bugzik, meg nem jo!!! (cameraTarget, stb. )
			changeCamNone();
			Vector3 camPos = new Vector3( dirS );
			camPos.mul( -6.0 );
			camPos.y += 1.5;
			camPos.add( pS );
			cam = new GameRef( map, GameRef.RID_CAMERA, camPos.toString() + "," + oriS.y + ",0,0, 0x02, 1.0,0.0,0.1", "no_bot_cam" );
			cam.command( "look " + map.id() + " " + pS.x + "," + (pS.y+1.0) + "," + pS.z + " 0,0,0" );
			cam.command( "move " + map.id() + " " + camPos.toString() + " 8" );
			cam.command( "render " + osd.getViewport().id() + " 0 0 1 " + (Viewport.RENDERFLAG_CLEARDEPTH | Viewport.RENDERFLAG_CLEARTARGET) );
		}
	}

	public int selectNrOpponent()
	{
		//ha ures a lista, generaljunk egyet!
		//a clubb[ok]bol szedjuk az ellenfeleket, de a kocsijuk nem a nappali, hanem egy erosebb jargany lesz!
		if( nrOpponents.size() == 0 )
		{
			int clubMin = player.club * GameLogic.CLUBMEMBERS;
			int clubMax = clubMin + GameLogic.CLUBMEMBERS;
			int playerID = GameLogic.findRacer( player );
			if (player.club < 2 && playerID == clubMax-1)
				clubMax += 1;
			int extra = -1;
//*
			float psr = (player.winPinkSlips+1)*(player.winPinkSlips+1)+1;

			int id;
			for (id = playerID+2; id >= playerID+1; id--)
				if (id < clubMax && id >= clubMin && id != playerID && GameLogic.speedymen[id].lastRaceDay < GameLogic.day) {
					nrOpponents.addElement( new Integer( id ));
					GameLogic.speedymen[id].enabledPinkSlips = 0;
//System.log("Pink: "+id+" - "+psr);
					if (id >= 30 && Math.random() < 1.0/psr) {
						GameLogic.speedymen[id].enabledPinkSlips = 1;
//System.log("Pink enabled!");
					}
				}
			for (id = playerID-1; id >= playerID-1; id--)
				if (id < clubMax && id >= clubMin && id != playerID && GameLogic.speedymen[id].lastRaceDay < GameLogic.day) {
					nrOpponents.addElement( new Integer( id ));
					GameLogic.speedymen[id].enabledPinkSlips = 0;
				}
/*			for (id = playerID+3; id <= playerID+5; id++)
				if (id < clubMax && id >= clubMin && id != playerID && GameLogic.speedymen[id].lastRaceDay < GameLogic.day) nrOpponents.addElement( new Integer( id ));
			for (id = playerID-2; id >= playerID-3; id--)
				if (id < clubMax && id >= clubMin && id != playerID && GameLogic.speedymen[id].lastRaceDay < GameLogic.day) nrOpponents.addElement( new Integer( id ));
/*

			// 0.2 valoszinuseggel valaszt legfeljebb 5-el felette allo ellenfelet
			// a sajat club-bol, vagy a kovetkezo club legelso versenyzojet
			if( Math.random() <= 0.2 )
			{
				extra = playerID + 3 + Math.random() * 3.0;
				if( extra > clubMax )
					extra = clubMax + 1;
				if( extra < GameLogic.speedymen.length )
					nrOpponents.addElement( new Integer( extra ));
			}

			// 3 opponents
			int need = 3; // + Math.random() * 3.0;

			int id = playerID + 3;
			if( id > clubMax )
				id = clubMax + 1;
			if( id > GameLogic.speedymen.length - 1 )
				id = GameLogic.speedymen.length - 1;

			while( id >= clubMin && nrOpponents.size() < need )
			{
				if( id != playerID && id != extra)
				{
					if( GameLogic.speedymen[id].lastRaceDay < GameLogic.day )
					{
						if( Math.random() <= 0.75 || ( id == clubMin && nrOpponents.size() == 0 ))
							nrOpponents.addElement( new Integer( id ));
					}
				}
				id--;
			}
*/
		}

/*		if( nrOpponents.size() != 0 )
		{
                        float pprestige = player.car.getPrestige();
                        if (pprestige == 0.0)
                           pprestige = 1.0;

//			System.log("pprestige = "+pprestige);

			int n = nrOpponents.size();
			for( int i=n-1; i>=0; i-- )
			{
				int bi = nrOpponents.elementAt(i).intValue();
				Bot b = (Bot)GameLogic.speedymen[bi];
				float p = b.nightVd.estimatePrestige() / pprestige;
				p *= p;

//				System.log("b.name = "+b.name);
				System.log("b.nightVd.estimatePrestige() = "+b.nightVd.estimatePrestige());
				System.log("p = "+p);
				if (p<0.8)
					System.log("you're too GOOD for this guy");
				if (p>5.0)
					System.log("you're too BAD for this guy");
				System.log("--------");

				if (p>5.0 || p<0.8)
					nrOpponents.removeElementAt(i);
			}
		}/**/

		if( nrOpponents.size() != 0 )
		{
			int n = nrOpponents.size();
			if (n > 3)
				n = 3;
			String[] s = new String[n];
			int[] botID = new int[n];
			ResourceRef[] pict = new ResourceRef[n];

			int[] cash = new int[4];

			int clubIndex = GameLogic.findRacer(player)/GameLogic.CLUBMEMBERS+1;

			cash[0] = 1000*clubIndex;
			cash[1] = 2000*clubIndex;
			cash[2] = 4000*clubIndex;
			cash[3] =    0;

			nrPrizeList = new int[n];

                        float pstrippenalty = 1.0;
                        if (player.car && player.car.chassis)
                           pstrippenalty -= player.car.chassis.C_drag;

				pstrippenalty *= pstrippenalty;

			for( int i = 0; i < n; i++ )
			{
				botID[i] = nrOpponents.elementAt(i).intValue();
                                Bot b = (Bot)GameLogic.speedymen[botID[i]];
				pict[i] = b.character;
				int rank = GameLogic.CLUBMEMBERS-(botID[i]%GameLogic.CLUBMEMBERS);	// hanyadik a rangsorban

				float odds = 1.000; // the factor of chance of your winning - the higher, the more sure you win //
				float pt, bt;

				if (player.car.bestNightQM < 0.100 || b.bestNightQM < 0.100)
					odds *= 3.000;
				else
					odds *= b.bestNightQM / player.car.bestNightQM;
				odds /= 1.0-(1.0-pstrippenalty)*0.5;

				/*
				System.log("stats for bot #"+i+"    "+nrOpponents.elementAt(i).intValue());
				System.log(" bot time/your time: "+(b.bestNightQM / player.car.bestNightQM));
				System.log(" strip penalty:      "+pstrippenalty);
				System.log(" so the odds are:    "+odds);
				*/

//				if (odds <= ODDS_PINKS && botID[i] >= 30 && Math.random() < 1.0/psr )
				if (odds <= ODDS_PINKS && GameLogic.speedymen[botID[i]].enabledPinkSlips )
					nrPrizeList[i] = cash[3];
				else
				if (odds <= ODDS_CASH_2)
					nrPrizeList[i] = cash[2];
				else
				if (odds <= ODDS_CASH_1)
					nrPrizeList[i] = cash[1];
				else
					nrPrizeList[i] = cash[0];

//				System.log(" and the bet is:     "+nrPrizeList[i]);

				String tmp;

				if( (rank % 10) == 1 )
					tmp = "st - ";
				else
				if( (rank % 10) == 2 )
					tmp = "nd - ";
				else
				if( (rank % 10) == 3 )
					tmp = "rd - ";
				else
					tmp = "th - ";

				if( (rank % 100) >= 11 && (rank % 100) <= 13 )
					tmp = "th - ";

				if( nrPrizeList[i] == 0 )
					s[i] = rank + tmp + b.name + " - PINK SLIPS";
				else
					s[i] = rank + tmp + b.name + " - $" + nrPrizeList[i];

//				if (config.majomParade)
				s[i] = s[i] + " - "+b.getPrestigeString(0)+" - "+b.nightVd.vehicleName;

//				s[i] = s[i] + " - "+String.timeToString( b.bestNightQM, String.TCF_NOMINUTES );
//				s[i] = s[i] + " - rumored best time: "+String.timeToString( b.bestNightQM, String.TCF_NOMINUTES );
			}

			int p = new NrOpponentDialog( player.controller, Dialog.DF_MODAL|Dialog.DF_FULLSCREEN, pict, s, nrPrizeList, player.money ).display();

			if( p < n )
			{
				//force to regenerate opponents list
				nrOpponents.removeAllElements();

				nrBotID1 = botID[p];
				nrPrize = nrPrizeList[p];

				GameLogic.speedymen[nrBotID1].lastRaceDay = GameLogic.day;

				return 1;	//oke. van ellenfel.
			}
		}
		else
		{
			new WarningDialog( player.controller, Dialog.DF_MODAL|Dialog.DF_DEFAULTBG, "NO OPPONENTS", "Sorry, there are no opponents to race against. \n Come back later!" ).display();
		}

		return 0;	//cancel, nincs ellenfel!
	}


	//hivodik: mindig ha indul egy fake verseny
	//watch race nyomas utan, ha epp nincs verseny a megnyomasakor
	//participate nyomas utan
	public void startNightRace( int playerRace )
	{
		int backState = nrStat;

		nrStat = NR_INVALID;

		nrPlayerRace = playerRace;

		Vector3 v = new Vector3();
		v.diff( player.car.getPos(), pS );
		float dist = v.length();

		//nezzuk, resztveszunk, vagy csak a kozelben vagyunk epp
		nrShowRaceStart = nrPlayerRace || nrWatching;// || (dist <= 25.0);

		if( nrShowRaceStart )
		{
			//gombok, pointerek off:
			//lehet hogy semmi sincs kint, de akkor sem zavar
			Input.cursor.enable(0);
			osd.hideGroup( nrWatchingGroup );
			osd.hideGroup( nightRaceGroup );

			//player osd off:
			//fontos, hanyszor hivjuk!

			//ha csak a kozelben vagyunk, letiltjuk az osd-t, a masik ket eset mar megtette ezt!
			if( !(nrWatching || nrPlayerRace) )
				enableOsd( 0 );
		}

    pl1 = null;
    pl2 = null;

		//mi is versenyzunk?
		if( nrPlayerRace )	//biztos, hogy (nrWatching==0)
		{
			if( !selectNrOpponent() )
			{	//nem mert, cancelt nyomott a user!

				nrWatching = backupnrWatching;
				Input.cursor.enable(1);	
				if( nrWatching )
				{	//watchbol nyomtunk participate-et
					osd.showGroup( nrWatchingGroup );
					enableOsd( 0 );
				}
				else
				{
					osd.showGroup( nightRaceGroup );
					//enableOsd(1);
				}
				nrStat  = backState;

				nrPlayerRace = 0;
				return;
			}

			nrHead1 = GameLogic.speedymen[nrBotID1].character;
			nrHead2 = player.character;
			nrName1 = GameLogic.speedymen[nrBotID1].name;
			nrName2 = player.name;

                        pl1 = GameLogic.speedymen[nrBotID1];
                        pl2 = player;
		}
		else
		{
			//find two racers from player's club
			int clubMin = player.club * GameLogic.CLUBMEMBERS;
			int clubMax = clubMin + GameLogic.CLUBMEMBERS - 1;
			int playerID = GameLogic.findRacer( player );
			nrBotID1 = Math.random() * GameLogic.CLUBMEMBERS;
			nrBotID1 = nrBotID1 + clubMin;
			if( nrBotID1 == playerID )
			{
				if( playerID == clubMax )
					nrBotID1 = playerID - 1;
				else
					nrBotID1 = playerID + 1;
			}
			int d = 1;
			if( nrBotID1 >= (clubMin + clubMax) / 2)
				d = -1;
			nrBotID2 = nrBotID1 + d;
			if( nrBotID2 == playerID )
				nrBotID2 += d;

			nrHead1 = GameLogic.speedymen[nrBotID1].character;
			nrHead2 = GameLogic.speedymen[nrBotID2].character;
			nrName1 = GameLogic.speedymen[nrBotID1].name;
			nrName2 = GameLogic.speedymen[nrBotID2].name;

                        pl1 = GameLogic.speedymen[nrBotID1];
                        pl2 = GameLogic.speedymen[nrBotID2];
		}


		RaceDialog	nrDialog;

		if( nrShowRaceStart )
		{
			applauseSfxID = applause1.play( pS, 150.0, 1.0, 1.0, SfxRef.SFX_LOOP | SfxRef.SFX_NOAUTOSTOP );  //start looped sfx
			applauseSfxOn = 1;

			//Input.cursor.enable(0);
			int pict = Math.random() * 5.0;
			nrDialog = new RaceDialog(player.controller, Dialog.DF_MODAL, new ResourceRef(RID_NRSTARTBG + pict), nrHead2, nrHead1, nrName2, nrName1, 0, 0, nrPrize, 0, pl2, pl1 );
			Frontend.loadingScreen.show( nrDialog );	//dialogus kitakar mindent!!
		}


		// stop current race (fake racers run)
		cleanupNightRace();


		// a versenyzok ne akadjanak ossze a player kocsijaval
		if( !nrPlayerPaused && !nrPlayerRace && dist <= 10.0 )
		{
			nrPlayerPaused = 1;
			//move player`s car
			Vector3 newPos = new Vector3( dirS );
			newPos.mul( -10.0 );
			newPos.add( pS );
			Vector3[] alignedPos = map.alignToRoad( newPos );
//			player.car.setMatrix( alignedPos[ 0 ], oriS );
			newPos.y = alignedPos[ 0 ].y;
			oriS.y += 0.5;
			player.car.setMatrix( newPos, oriS );
			oriS.y -= 0.5;
			player.car.command( "stop" );
		}


		//lady+versenyzok letrehozasa/beallitasa

		Vector3[] alignedPos = map.alignToRoad( pS );
		Vector3 botPos, tmp;

		//lajdi
		if( !nrStarterLady )
		{
			botPos = new Vector3( alignedPos[ 0 ] );

			tmp = new Vector3( alignedPos[ 1 ] );
			tmp.mul( 5.0 );

			botPos.add( tmp );

			nrStarterLady=new RenderRef( map, humans:0x0070r, "gizi" );
			nrStarterLady.setMatrix( botPos, new Ypr(tmp) );
			nrStarterLadyAnim = new Animation( nrStarterLady, new ResourceRef(humans:0x007cr) );
		}


		//racers		
		botPos = new Vector3( alignedPos[ 0 ] );

		tmp = new Vector3( alignedPos[ 1 ] );
		tmp.mul( 2.0 );
		tmp.rotate( new Ypr(1.57,0.0,0.0) );

		//1
		botPos.add( tmp );

		float	yNudge;
		int		idx, colorSeed;

		if( nrPlayerRace )
		{	//"igazi" club versenyzo
			nrBot1 = GameLogic.speedymen[nrBotID1];
			nrBot1.setDriverObject( GameLogic.HUMAN_OPPONENT );

			//nem a 'nappali' kocsijaval all ki!
			int	botIndex = GameLogic.findRacer( nrBot1 );
			Vehicle nightVhc = new Vehicle( this, nrBot1.nightVd.id,  nrBot1.nightVd.colorIndex, nrBot1.nightVd.optical, nrBot1.nightVd.power, nrBot1.nightVd.wear, nrBot1.nightVd.tear );
                        nightVhc.races_won = nrBot1.nightWins;
                        nightVhc.races_lost = nrBot1.nightLoses;

			nrBot1.createCar( map, nightVhc );

			nrBot1.car.setMatrix( botPos, oriS );
			nrBot1.car.setParent( map );
			nrBot1.car.command( "reload" );
			nrBot1.car.command( "stop" );
			nrBot1.car.command( "idle" );
			mRaceBot = nav.addMarker( nrBot1 );	//add night opponent bot's marker
		}
		else
		{	//fake racer
			yNudge = (Math.random()*2-1)*yawNudge;
			idx = Math.random()*fakers.length;
			colorSeed = Math.random()*12345;

			oriS.y+=yNudge;
			GameRef car1 = new GameRef( map, fakers[idx], botPos.toString() + ","+oriS.y+",0,0," + colorSeed, "nightracer1" );
			car1.setMatrix( botPos, oriS );
			oriS.y-=yNudge;

			nrBot1 = GameLogic.speedymen[nrBotID1];
			nrBot1.setDriverObject( GameLogic.HUMAN_OPPONENT );
			nrBot1.createCar( map, new Vehicle(car1) );
			nrBot1.car.command( "stop" );
			nrBot1.car.command( "idle" );
		}

		//2
		botPos.sub( tmp );
		botPos.sub( tmp );

		if( nrPlayerRace )
		{	//player
			//player.car.command( "reset" );
			player.car.setMatrix( botPos, oriS );
			player.car.command( "stop" );
			player.car.command( "idle" );
			nrPlayerRace = 2;
		}
		else
		{	//fake racer
			yNudge = (Math.random()*2-1)*yawNudge;
			idx = Math.random()*fakers.length;
			colorSeed = Math.random()*12345;

			oriS.y+=yNudge;
			GameRef car2 = new GameRef( map, fakers[idx], botPos.toString() + ","+oriS.y+",0,0," + colorSeed, "nightracer1" );
			car2.setMatrix( botPos, oriS );
			oriS.y-=yNudge;

//			nrBot2 = new Bot( 0, 12345, 1.0 );
			nrBot2 = GameLogic.speedymen[nrBotID2];
			nrBot2.setDriverObject( GameLogic.HUMAN_OPPONENT );
			nrBot2.createCar( map, new Vehicle(car2) );
			nrBot2.car.command( "stop" );
			nrBot2.car.command( "idle" );
		}

		nrFinishTrigger = addTrigger( pF, null, Marker.RR_FINISH, "event_handlerNrFinish", 8, "night race finish trigger" );
		nrFinishSoundTrigger = addTrigger( pF, null, Marker.RR_FINISH, "event_handlerNrFinishSound", 30, "night race finish sound trigger" );

		if( nrShowRaceStart )
		{
			backupCamera();

			if( nrPlayerRace )
			{	//participating
				changeCamTarget(player.car);
				changeCamTarget2(nrBot1.car);
				changeCamFollow();
			}
			else
			if( nrWatching )
			{
				lookBot( null, 1 );
				cameraTarget = nrBot1.car;
				cameraTarget2 = nrBot2.car;
//				changeCamTarget(nrBot1.car);	//temp.needed
//				changeCamTarget2(nrBot2.car);
				nrLookAt = 0;
			}
			else
			{	//csak a kozelben vagyok (unused)
				//cam.command( "look " + nrBot2.car.id() );
				changeCamTarget2(nrBot1.car);
				changeCamChase();
			}

			Frontend.loadingScreen.display( nrDialog, 10.0 );

			nrStat = NR_3;
			if( nrPlayerRace )	//resztveszunk
			{
				if (cam)
				{
					changeCamTarget2(nrBot1.car);
					cam.command( "angle 0 4.0 0.7853" );		//0.7853 = (2*pi)/8.0
					cam.command( "dist 5.5 6.5");
				}
				nrDelay = 7;
			} 
			else
			{
				Input.cursor.enable(1);
				if( nrWatching )
				{
					osd.showGroup( nrWatchingGroup );
				}
				else
				{
//					osd.showGroup( nightRaceGroup );
//					enableOsd( 1 );
				}
				nrDelay = 1;
			}

		}
		else
		{
			nrStat = NR_START;
			nrDelay = 0;
		}
	}

    public void startNightRace2()
    {
		nrFinished1 = 0;
		nrFinished2 = 0;

		if( nrShowRaceStart )
		{
			setMessage( "GO!" );
			speechGO.play();
		}

		nrStartTime = System.simTime();

		if( nrPlayerRace )
		{
			nrBot1.car.command( "start" );
			nrBot1.startRace( pF, player );
			nrBot1.brain.command( "AI_NightRace" );
			player.car.setCruiseControl(0);
			player.car.command( "start" ); 
			cam.command( "angle 0 0" );
			cam.command( "dist 8.0 16.0");
		} 
		else
		{
			nrBot1.car.command( "start" );
			nrBot2.car.command( "start" );
			nrBot1.startRace( pF, nrBot2 );
			nrBot2.startRace( pF, nrBot1 );

			if( !nrWatching )
			{
				//todo: alljon vissza normalis allasba
			}
		}

		Sound.changeMusicSet( Sound.MUSIC_SET_RACE );
	}

    public void finishNightRace()
	{
		nrStat = NR_INVALID;

		RaceDialog	nrDialog;
		int background;
		nrQuit = 0;

		//mert itt lehetne egy is??!
		if( nrPlayerRace == 2 )
		{
			if( nrTime2 < nrTime1 )
				background = RID_NRFIN_WIN;
			else
				background = RID_NRFIN_LOSE;
		} 
		else 
		{
			background = RID_NRFIN_AI;
		}

		if( nrShowRaceFinish )
		{
			if( nrPlayerRace == 2 || nrWatching )
			{
				cam.command( "angle 0 0" );
				cam.command( "dist 8.0 16.0");
			}

			if( nrWatching )
			{
				nrBot1.brain.command("camera 0");
				nrBot2.brain.command("camera 0");
			}
			else
				enableOsd( 0 );

			if( nrTime2 < nrTime1 )
				nrDialog = new RaceDialog(player.controller, Dialog.DF_MODAL, new ResourceRef(background), nrHead2, nrHead1, nrName2, nrName1, nrTime2, nrTime1, nrPrize, 1, pl2, pl1 );
			else
				nrDialog = new RaceDialog(player.controller, Dialog.DF_MODAL, new ResourceRef(background), nrHead1, nrHead2, nrName1, nrName2, nrTime1, nrTime2, nrPrize, 1, pl1, pl2 );

			Frontend.loadingScreen.show( nrDialog );
		} 

		if( nrPlayerRace == 2 )
		{
                        if (nrBot1.bestNightQM == 0.0 || nrTime1 < nrBot1.bestNightQM)
                           nrBot1.bestNightQM = nrTime1;
			if( nrTime2 < nrTime1 )
			{
                	        if (player.car.bestNightQM == 0.0 || nrTime2 < player.car.bestNightQM)
	                           player.car.bestNightQM = nrTime2;
				//player won
				speechYOUWIN.play();

				player.car.races_won++;
				player.races_won++;
                                nrBot1.nightLoses++;
                                nrBot1.car.races_lost++;

				//csak hogy legyen valami eletszaga... (mert egyebkent most a semmibol hoztuk letre..)
//				nrBot1.car.races_lost = 1+Math.random()*nrBot1.fileid*(1.0-nrBot1.aiLevel);
//				nrBot1.car.races_won =    Math.random()*nrBot1.fileid*nrBot1.aiLevel;
				nrBot1.brain.command("camera 0");

				if( nrPrize > 0 )
				{
					player.money += nrPrize;
				}
				else
				{	//won opponents car!
					player.winPinkSlips++;
					nrBot1.leaveCar(0);
					player.carlot.addCar( nrBot1.car );
					nrBot1.car = null;
					player.carlot.saveCar( player.carlot.curcar );
					player.carlot.flushCars();
				}
			}
			else
			{
				//player lose
				player.car.races_lost++;
				player.races_lost++;
                                nrBot1.nightWins++;
                                nrBot1.car.races_won++;

				if( nrPrize > 0 )
				{
					player.money -= nrPrize;
				}
				else
				{	//loose car, back to garage!
					nrQuit = 1;
				}
			}

			// (vagy leallitjuk a fizikat MOST, es a dialogus alatt szepen letesszuk az ut szelere, hogy ne torje ossze magat,
			// vagy csak beallitunk egy flag-et, hogy majd ha magatol megallt, dobjuk be a dialogust.)

		    GameLogic.challenge( GameLogic.findRacer(player), GameLogic.findRacer(nrBot1), 0, (nrTime2 < nrTime1), 1 );
			refreshStatus();	//ITT is lehet.

		}
		else
		{
			GameLogic.challenge( GameLogic.findRacer(nrBot2), GameLogic.findRacer(nrBot1), 0, (nrTime2 < nrTime1), 1 );
		}


		//reset parking cars
		Vector3 playerPos = player.car.getPos();
		for( int i = 0; i < parkingCars.length; i++ )
		{
			if( parkingCars[ i ] )
				parkingCars[ i ].reset( playerPos );
		}


		cleanupNightRace();	//cleans up the bots, their cars and the trigger areas


		//jo lesz ez itt, a dialogus ugyis lefagyasztja a fizikat,...
		if( nrShowRaceFinish )
		{
			//Thread.sleep( 4000 );

			if( nrPlayerRace==2 )
			{
				player.car.command( "start" );
				nrPlayerRace = 0;
			}

			if( nrWatching )
			{
				player.controller.command( "camera " + cam.id() );
				restoreCamera();
			} 
			else
			{
				enableOsd( 1 );
			}

			Frontend.loadingScreen.display( nrDialog, 10.0 );
		}

		if( nrQuit )
		{
			Thread.sleep( 1000 );
			killCar = 1;
			GameLogic.changeActiveSection( GameLogic.garage );
		}
		else
		{
			nrDelay = 15;		//szunet a kov verseny kezdeseig!!!
			nrStat = NR_IDLE;
		}
	}

	public void cleanupNightRace()
	{
		if( nrBot1 )
		{
			nrBot1.deleteCar();

            if( mRaceBot )
            {
                nav.remMarker( mRaceBot );	//rem night opponent bot's marker
                mRaceBot=null;
            }
		}
		if( nrBot2 )
			nrBot2.deleteCar();
		if( nrFinishTrigger )
			removeTrigger( nrFinishTrigger );
		if( nrFinishSoundTrigger )
			removeTrigger( nrFinishSoundTrigger );
		nrBot1=nrBot2=nrFinishTrigger=nrFinishSoundTrigger=null;
	}


	public void nightRaceStep()
	{
		if( nrDelay > 0 )
		{
			nrDelay--;
			return;
		}

		if( nrStat == NR_IDLE )
		{
			if( player.car.getPos().sub( pS ).length() < 500.0f )
			{
				startNightRace( 0 );
			}
			else
			{
				nrDelay = 10;
			}
		}
		else
		if( nrStat == NR_3 )
		{
			if( nrPlayerRace )
				cam.command( "angle 0 3.0" );

			setMessage( "3" );
			speech3.play();

			if( nrBot1 )
	            nrBot1.brain.command( "AI_BeginRace 0.5" );
			if( nrBot2 )
	            nrBot2.brain.command( "AI_BeginRace 0.5" );
			
			nrStat = NR_2;
			nrDelay = 0;
		}
		else
		if( nrStat == NR_2 )
		{
			setMessage( "2" );
			speech2.play();

			nrStarterLadyAnim.setSpeed( 2.0 );
			nrStarterLadyAnim.play();
			
			nrStat = NR_1;
			nrDelay = 0;
		}
		else
		if( nrStat == NR_1 )
		{
			setMessage( "1" );
			speech1.play();

			if( nrBot1 )
	            nrBot1.brain.command( "AI_BeginRace 1.0" );
			if( nrBot2 )
	            nrBot2.brain.command( "AI_BeginRace 1.0" );

			nrStat = NR_START;
			nrDelay = 0;
		}
		else
		if( nrStat == NR_START )
		{
			startNightRace2();
			nrStat = NR_RACE;
			nrDelay = 1;
		}
		else
		if( nrStat == NR_RACE )
		{
			if( nrStarterLady )
			{
				nrStarterLady.destroy();
				nrStarterLady = null;
				nrStarterLadyAnim = null;
			}

			if (nrPlayerPaused && !nrWatching )
			{
				player.car.command( "start" ); 
				nrPlayerPaused = 0;
			}


			if( nrWatching )
			{
				//mindig a hatso versenyzot nezzuk (igy valszeg a masikat is lathatjuk..)
				if( nrBot1 && nrBot2 && !nrFinished1 && !nrFinished2 )
				{
					Vector3 v1 = new Vector3();
					Vector3 v2 = new Vector3();
					v1.diff( nrBot1.car.getPos(), pF );
					v2.diff( nrBot2.car.getPos(), pF );
					float d1 = v1.length();
					float d2 = v2.length();
					if( d1 > d2 )
					{
						if( nrLookAt != 1 )
						{
							if (cameraTarget)
								changeCamTarget2(nrBot2.car);
							lookBot( nrBot1, 0 );
							nrLookAt = 1;
						}
					}
					else
					{
						if( nrLookAt != 2 )
						{
							if (cameraTarget)
								changeCamTarget2(nrBot1.car);
							lookBot( nrBot2, 0 );
							nrLookAt = 2;
						}
					}
				}
			}
		}
		else
		if( nrStat == NR_FINISH )
		{
			finishNightRace();
		}
	}

    //ha valaki a cel kozelebe ert
    public void event_handlerNrFinishSound( GameRef obj_ref, int event, String param )
    {
		int	id = param.token(0).intValue();

		if( event == EVENT_TRIGGER_ON )
		{
			if( nrStat == NR_RACE )
			{
				int startSound;
				int	id = param.token(0).intValue();

				if ( id == nrBot1.car.id() )
				{
					startSound = 1;
				}

				if ( nrPlayerRace == 2 )
				{
					if ( id == player.car.id() )
					{
						startSound = 1;
					}
				}
				else
				{
					if ( id == nrBot2.car.id() )
					{
						startSound = 1;
					}
				}
				if( startSound )
				{
					applause2.play( pF, 150.0, 1.0, 1.0, 0 );
				}
			}
		}
	}

    //ha valaki beert a celba
    public void event_handlerNrFinish( GameRef obj_ref, int event, String param )
    {
		int	id = param.token(0).intValue();

		if( event == EVENT_TRIGGER_ON )
		{
			if( nrStat == NR_RACE )
			{
				int	id = param.token(0).intValue();

				if( !nrFinished1 )
					if ( id == nrBot1.car.id() )
					{
						nrTime1 = System.simTime() - nrStartTime;
						nrFinished1 = 1;
						nrBot1.stop();

						if( !nrFinished2 )
							addTimer( 10, 7 );
					}

				if( !nrFinished2 )
					if ( nrPlayerRace == 2 )
					{
						if ( id == player.car.id() )
						{
							nrTime2 = System.simTime() - nrStartTime;
							nrFinished2 = 1;
							player.car.command( "brake" );

							if( !nrFinished1 )
								addTimer( 10, 7 );
						}
					}
					else
					{
						if ( id == nrBot2.car.id() )
						{
							nrTime2 = System.simTime() - nrStartTime;
							nrFinished2 = 1;
							nrBot2.stop();

							if( !nrFinished1 )
								addTimer( 10, 7 );
						}
					}

				if( nrFinished1 && nrFinished2 )
				{
					//nehogy tobsszorosen hivodjon!
					nrStat = NR_INVALID;

					Vector3 v = new Vector3();
					v.diff( player.car.getPos(), pF );
					float dist = v.length();

					//Vector3 u = osd.getViewport().unproject( pS, cam.getInfo( GII_CAMERA ) );
					//System.log( u.toString() );

					//8 mp forgas
					nrShowRaceFinish = (nrPlayerRace == 2 || nrWatching || ((dist <= 25.0) && player.car.getSpeedSquare() < TINY_SPEED_SQ));		//PJ!!

					if( nrShowRaceFinish )
					{
						if( nrPlayerRace == 2 || nrWatching )
						{
							changeCamFollow();	//hatha epp tv-zett verseny kozben, stb.

							cam.command( "dist 2.5 10.0");
							cam.command( "smooth 0.5 0.5");
							cam.command( "force 1.6 0.5 -0.7" );	//defaults are in config.java
							cam.command( "torque 0.05" );
							cam.command( "angle 0 4.0 0.7853" );		//0.7853 = (2*pi)/8.0
							cam.command( "dist 5.5 6.5");
							nrDelay = 8;
						}
					}
					nrStat = NR_FINISH;
				}
			}
		}
	}

	//user versenyezni akar?
    public void event_handlerNrStart( GameRef obj_ref, int event, String param )
    {
		int	id = param.token(0).intValue();
		
		if( event == EVENT_TRIGGER_ON )
		{
			if ( id == player.car.id() )
			{
				if( !nrPlayerRace )
				{
					nrNear = 1;
					osd.showGroup( nightRaceGroup );
					Input.cursor.enable(1);
				}
			}
		}
		else if( event == EVENT_TRIGGER_OFF )
		{
			if ( id == player.car.id() )
			{
				if( !nrPlayerRace )
				{
					if( nrWatching )
					{
						if( nrBot1 && nrBot2 )
						{
							nrBot1.brain.command("camera 0");
							nrBot2.brain.command("camera 0");
						}

						restoreCamera();
			
						osd.hideGroup( nrWatchingGroup );
						nrWatching = 0;
						enableOsd( 1 );
					} 
					else
					{
						osd.hideGroup( nightRaceGroup );
					}
					Input.cursor.enable(0);
					nrShowRaceStart = 0;
					nrShowRaceFinish = 0;
					nrNear = 0;
				}
			}

			if( nrStat == NR_RACE && applauseSfxOn )
			{
				int applauseOff;

				if ( id == nrBot1.car.id() )
				{
					applauseOff = 1;
				}
				if ( nrPlayerRace == 2 )
				{
					if ( id == player.car.id() )
					{
						applauseOff = 1;
					}
				}
				else
				{
					if ( id == nrBot2.car.id() )
					{
						applauseOff = 1;
					}
				}
				if( applauseOff )
				{
					applause1.stop( applauseSfxID );  //end looped sfx
					applauseSfxOn = 0;
				}
			}
		}
	}

    public void event_handlerRaceFinish( GameRef obj_ref, int event, String param )
    {
		if( raceState == 1)
		{
            int	id = param.token(0).intValue();
            int	rank;   //player hanyadik lett

            if (id == player.car.id())
                    rank=1;
            else
            if (id == raceBot.car.id())
                    rank=2;
			else
            if (demoBot && (id == demoBot.car.id()))
                    rank=1;

            if( rank )      //a ket versenyzo kozul ert be valaki?
            {
				raceState = 2;

			//ToDo: brake, slow motion, timer, chase camera

//ALWAYS FROM NOW				if ( GameLogic.gameMode == GameLogic.GM_DEMO )
//				{
					System.timeWarp( 0.1 );
					addTimer( 0.1*10.0, 14 );
					if (player && (id == player.car.id()))
					{
						player.car.command( "brake" );
						changeCamTarget(player.car);
						changeCamTarget2(raceBot.car);	
					} else
					if (demoBot && (id == demoBot.car.id()))
					{
						demoBot.car.command( "brake" );
						demoBot.stop();
						changeCamTarget(demoBot.car);
						changeCamTarget2(raceBot.car);	
					} else
					if (raceBot && (id == raceBot.car.id()))
					{
						raceBot.car.command( "brake" );
						raceBot.stop();
						changeCamTarget(raceBot.car);	
						if (demoBot && demoBot.car)
							changeCamTarget2(demoBot.car);
						else
						if (player.car)
							changeCamTarget2(player.car);
					}
					changeCamNone();
					lastCamPos = obj_ref.getPos();
					lastCamPos.y += 2.0f;
					changeCamChase();
					if (cam)
						cam.command( "simulate 1" );

//					cleanupRace();
//					GameLogic.changeActiveSection( parentState );

				if ( GameLogic.gameMode == GameLogic.GM_DEMO )
				{
					return;
				} else
				{
					player.car.command( "brake" );

					int challenger_won = !( challenger == player ^ rank==1 );

					if( rank==1 )
					{
						speechDAYYOUWIN.play();
						raceDialog = new RaceDialog(player.controller, Dialog.DF_MODAL|Dialog.DF_FREEZE, new ResourceRef(RID_DAY_WIN), player.character, raceBot.character, player.name, raceBot.name, 0.0, 0.0, prize, 5, player, raceBot );
					} else
					{
						speechDAYYOULOOSE.play();
						raceDialog = new RaceDialog(player.controller, Dialog.DF_MODAL|Dialog.DF_FREEZE, new ResourceRef(RID_DAY_LOOSE), raceBot.character, player.character, raceBot.name, player.name, 0.0, 0.0, prize, 5, raceBot, player );
					}

					if( GameLogic.gameMode == GameLogic.GM_CARREER )
					{
						if( rank==1 )
						{
							player.money+=prize;
							player.car.races_won++;
							player.races_won++;
						}
						else
						{
							player.money-=prize;
							player.car.races_lost++;
							player.races_lost++;
						}

						GameLogic.challenge( GameLogic.findRacer(challenger), GameLogic.findRacer(challenged), abandoned, challenger_won, 0 );
						refreshStatus();	//ITT is lehet.
					}
				}

/*
		        raceState = 0;

                int challenger_won = !( challenger == player ^ rank==1 );

				if( GameLogic.gameMode == GameLogic.GM_CARREER )
					GameLogic.challenge( GameLogic.findRacer(challenger), GameLogic.findRacer(challenged), abandoned, challenger_won, 0 );


                if( rank==1 )
                {
					new SfxRef( RID_SFX_DAY_WIN ).play();
                    player.money+=prize;
                    player.car.races_won++;
                    player.races_won++;

					raceDialog = new RaceDialog(player.controller, Dialog.DF_MODAL|Dialog.DF_FREEZE, new ResourceRef(RID_DAY_WIN), player.character, raceBot.character, player.name, raceBot.name, 0.0, 0.0, prize, 5, player, raceBot );
                }
                else
                {
					new SfxRef( RID_SFX_DAY_LOOSE ).play();
                    player.money-=prize;
                    player.car.races_lost++;
                    player.races_lost++;

					raceDialog = new RaceDialog(player.controller, Dialog.DF_MODAL|Dialog.DF_FREEZE, new ResourceRef(RID_DAY_LOOSE), raceBot.character, player.character, raceBot.name, player.name, 0.0, 0.0, prize, 5, raceBot, player );
                }

				Frontend.loadingScreen.show( raceDialog );
				cleanupRace();
				refreshStatus();	//ITT is lehet.
				Frontend.loadingScreen.userWait();

				if( GameLogic.gameMode != GameLogic.GM_QUICKRACE )	//nem valtunk vissza, ugyis uj verseny indul nehany masodperc mulva!!
					Sound.changeMusicSet( Sound.MUSIC_SET_DRIVING );

				if(	GameLogic.gameMode == GameLogic.GM_QUICKRACE )
				{
			        addTimer( 3, 8 );	//nem jo a sleep, mert kozben eloveheti az ingame menut!!
				}
*/			}
		}
    }

	public void startQuickRace()
	{
		Frontend.loadingScreen.show();
		createQuickRaceBot();

		raceState=1;

		//racesetupos:
		if( !GameLogic.racesetup )
			GameLogic.racesetup=new RaceSetup();
		GameLogic.changeActiveSection( GameLogic.racesetup );

		//quick:
		/*
		Vector3 pStart = map.getNearestCross( player.car.getPos() );
		Vector3 pFinish = map.getNearestCross( pStart, 500 );
		startRace( pStart, pFinish, 0 );
		Frontend.loadingScreen.track();
		*/
	}

	//called by racefinish, or exit()
	public void cleanupRace()
	{
        abandoned=1;	//sorozatos ujrakihivasok elkerulesere

//		System.log("cleanuprace");

		if (!raceState) return;
        raceState=0;

		if (raceBot)
		if (raceBot.dummycar)
		{
			if (raceBot.dummycar.id() != raceBot.car.id())
			{//ToDo: delete real racecar, unhide dummycar, move it to racecar's pos
				Vector3	pos = raceBot.car.getPos();
				Ypr		ori = raceBot.car.getOri();

				raceBot.deleteCar();//destroy racecar, brain, everything

				raceBot.dummycar.command( "reset" );
				raceBot.dummycar.setMatrix( pos, ori );
				raceBot.dummycar.setParent( map );

				
				//raceBot.createCar( map, new Vehicle(raceBot.dummycar) );	//!Vehicle lett a gamerefbol! gc!!

				raceBot.car = new Vehicle(raceBot.dummycar);
				//enterCar( car ); lassu!
				raceBot.brain = new GameRef( map, sl:0x0000006Er, "", "BOTBRAIN");
		        RenderRef render = new RenderRef( map, raceBot.driverID, "botfigura-afterrace" );
				raceBot.brain.command( "renderinstance " + render.id() );

				raceBot.brain.command( "controllable " + raceBot.dummycar.id() );

				raceBot.traffic_id = 0;
			}

			if( mRaceBot )
			{
                nav.remMarker( mRaceBot );	//rem day bot's _real_ car's marker
                mRaceBot=null;
			}

	        raceBot.reJoinTraffic();
		    raceBot.setTrafficBehaviour( GameRef.TC_PASSIVE );      //huzzon el, hogy ujrageneralodhasson
		}
		else
		{
			destroyRaceBot();
		}

		if (trRaceFinish)
		{
			trRaceFinish.finalize();
			trRaceFinish = null;
		}

        nav.remMarker( mStart );
        nav.remMarker( mFinish );
		if (nav.route)
		{
			nav.route.destroy();
			nav.route = null;
		}

        finishObject.destroy();

		//megallitjuk a kocsijat
		//nem jo, mert ha utkozott (lampaoszlop) es felcsapodott a levegobe, odafagy a resetelees miatt!
        //player.car.command( "reset" );


		//utana ugyis a garazsba kerul! (ha ingame-menu hivta)
		//Sound.changeMusicSet( Sound.MUSIC_SET_DRIVING );
	}

    //a race setup hivja, ha nem merunk kiallni
    public void abandonRace()
    {
        GameLogic.challenge( GameLogic.findRacer(challenger), GameLogic.findRacer(challenged), 1, 0, 0 );
		refreshStatus();	//ITT is lehet.
        raceState=0;
        raceBot.setTrafficBehaviour( GameRef.TC_PASSIVE );      //huzzon el, hogy ujrageneralodhasson
        abandoned=1;

		if (nav.route)
		{
			nav.route.destroy();
			nav.route = null;
		}

    }

	public void backupCamera()	//before watch/participate night race
	{
		if( !backupCam )
		{
			backupCam = 1;

			nrcameraMode_before = cameraMode;
			nrcameraTarget = cameraTarget;
		}
	}

	public void restoreCamera()	//when leaving night race
	{
		if( backupCam )
		{
			backupCam = 0;

			cameraTarget = nrcameraTarget;
			cameraMode = nrcameraMode_before;
			if (cameraMode == CAMMODE_FOLLOW)
				changeCamFollow();
			else
			if (cameraMode == CAMMODE_INTERNAL)
				changeCamInternal();
			else
			if (cameraMode == CAMMODE_TV)
				changeCamTV();
			else
				changeCamFollow();	//default

/*
			Vector3 camPos = new Vector3( dirS );
			camPos.mul( -18.0 );
			camPos.y+=5;
			camPos.add( pS );
			cam.setMatrix( camPos, oriS );
			cam.command( "look " + player.car.id() + " 0,0,0 0,0,0 0.2" );
			cam.command( "move " + player.car.id() + " 0,0,0 3.5" );
*/
		}
	}

	public int[] calculateFineSum( int clear )
	{
		int[] fine = new int[5];

		if( overSpeed )
		{
			//5 dollars @ every km/h exceed
			fine[1] = overSpeed*3.6*5 + 30;
		}

		fine[2] = crashes * 100;

		//itt persze a felszolitastol a megallasig eltelt idot kellene alapul venni;
		//neha eltart egy ideig, mig ulotler a rendor...
		if( fleedAway )
		{
			float	tm;
			
			if( pullOverTime )
				tm = pullOverTime;
			else
				tm = System.simTime();

			fine[3] = 200 + (tm - firstAlertTime) * 10;
		}

		fine[4] = 0.0;
		if( System.simTime() - firstAlertTime > 1.0*60.0) // the police does a finer check not only a routine check if chase was at least 1 minute long. every 30 seconds the thoroughness is raised 100% (not clamped, sorry, who cares, limit handles the thing) - Sala
		{
			if (GameLogic.player)
				if (GameLogic.player.car)
					fine[4] = GameLogic.player.car.calcPoliceFine( (System.simTime() - firstAlertTime - 1.0*60.0)/30.0 );
		}

		fine[0] = fine[1]+fine[2]+fine[3]+fine[4];

		if( clear )
		{
			overSpeed=0;
			crashes=0;
			fleedAway=0;
		}

		return fine;
	}
    
	int timeLock;

	public void handleEvent( GameRef obj_ref, int event, int param )
    {
        super.handleEvent( obj_ref, event, param );



        if( event == EVENT_TIME )
        {
			switch( param )
			{
			case 2:
				if( timeLock )
				{
//					System.log( "skipping dangerous timer event..." );
				}
				else
				{
					timeLock = 1;

					time++;

					//if( (time % 10 == 0) && collision )
					//{
					//	collision = 0;
					//	refreshStatus();
					//}

					//globalis, jol atgondolt megoldas szukseges:
					//ha pl a user az ingame-menube ugrik, hogyan pauseoljuk le a jatekot, a timereket?!?!
					if( GameLogic.actualState == this )
					{
						if( nightTime )
						{
							nightRaceStep();

							if( nrQuit )	//patch, ha kileptunk mar (kocsit vesztettunk), semmi keresnivalonk itt!
							{
								timeLock = 0;
								break;
							}
						}

						float playerSpeedSq = player.car.getSpeedSquare();
						int	actScout;

						if(  policeState || playerSpeedSq > SPEED_LIMIT_SQ )
						{
							alertPolice();	//try to alert a police scout
						}

						for( int i=alertedScouts.size()-1; i>=0; i-- )
						{
							PoliceScout pc = alertedScouts.elementAt( i );

							if( pc.returningTraffic )
							{
								if( pc.bot.traffic_id )
								{
									//mivel 1 masodpercenkent vizsgaljuk, hogy visszament-e, lehet, hogy
									//azota mar torlodott is.. ezt azonban majd az alertpolice vizsgalja!
									alertedScouts.removeElementAt(i);
									//pc.bot.leaveCar(1);
									policeCars.addElement( pc.tracker );
									//setMessage( "Pleft" );

									//TODO: ha x ideig nem ert vissza, es nem is lathato, toroljuk!
									//NEED: support a lathatosag lekerdezesehez!
								}
								else
								{
									//lehet, hogy valahol fejreall, stb: szenved, es nem tud viszamenni!
									Vector3 v = pc.bot.car.getPos();
									v.sub( player.car.getPos());
									float distance = v.length();
									if( distance > 600 )
									{
										sleepPoliceScoutQuick( pc );
									}
								}
							}
							else
							{
								if( !pc.bot.car.id() )
									System.exit( "s elhulltanak legjobbjaink a hosszu harc alatt: " + Integer.toHexString(pc.bot.debugid) + "\n" );
								pc.distance = map.getRouteLength( pc.bot.car.getPos(), player.car.getPos() );
								
								actScout++;

								if( pc.distance >= 0)	//oda tud menni?
								{
									//messzebbrol nem erzekeli a pontos sebesseget
									if( pc.distance < 300.0 )
									{
										float maxSpeed = player.car.hasCrime()*1.1;
										if( maxSpeed >= 0 )
										{
											maxSpeed = Math.sqrt(playerSpeedSq)-maxSpeed;
											if( maxSpeed > overSpeed )
												overSpeed = maxSpeed;
										}
									}

									if( fleedAway )
									{
										if( !pullOverTime )
										{
											if( playerSpeedSq < TINY_SPEED_SQ )
												pullOverTime = System.simTime();
										}
										else
										{
											//ujra elindult a barom
											if( playerSpeedSq >= TINY_SPEED_SQ )
												pullOverTime = 0.0;
										}
									}
									else
									if( playerSpeedSq >= TINY_SPEED_SQ )
									{
										//tobb mint 10 masodperce nem allt meg...
										if( System.simTime() - firstAlertTime > 10.0 )
										{
											fleedAway=1;
											pullOverTime = 0.0;
										}
									}

									if( pc.distance < 10.0 )
									{
										if( playerSpeedSq < TINY_SPEED_SQ )
										{
											if( pc.bot.car.getSpeedSquare() < TINY_SPEED_SQ )
											{
												policeState=0;
												roamFree=1;
												addTimer( 5, 13 );	//roamfree timeout

												if ( GameLogic.gameMode != GameLogic.GM_DEMO )
												{
													int[] fine = calculateFineSum( 1 );

													int osdState = osdEnabled;
													enableOsd( 0 );
													//ControlSetState	css=player.controller.reset();
													//player.controller.activateState( ControlSet.MENUSET );
													Frontend.loadingScreen.display( new FineDialog( player.controller, Dialog.DF_MODAL|Dialog.DF_FREEZE|Dialog.DF_FULLSCREEN, player.name, fine ), 10.0 );
													//player.controller.reset( css );
													enableOsd( 1 );

													//megbuntetjuk, de nem engedjuk negativba!
													player.money -= fine[0];
													if( player.money < 0 )
														player.money = 0;

													player.decreasePrestige(Racer.PRESTIGE_STEP);
													refreshStatus();	//ITT is lehet.
												}

												//sleep all scouts and abort iteration now!
												for( int i=alertedScouts.size()-1; i>=0; i-- )
												{
													PoliceScout pc = alertedScouts.elementAt( i );
													sleepPoliceScout( pc );
												}
												break;
											}
										}
									}
									else
									if( pc.distance < 100.0 )
									{       //utolerte?
										////tamas-shot-pecs!
										if ( GameLogic.gameMode != GameLogic.GM_DEMO )
											if( policeState==10 )
												setMessage( "POLICE: PULL OVER NOW!" );
									}
									else	//police get lost distance! sync with initial value!
									if ( pc.distance > 400.0+100.0*player.club )
									{
										//itt igazabol a lathatosagat (is) kellene teszteni
										Vector3 v = pc.bot.car.getPos();
										v.sub( player.car.getPos());
										float distance = v.length();
										if( distance > 600 )
										{
											sleepPoliceScoutQuick( pc );
										}
										else
											sleepPoliceScout( pc );
									}
								}
							}
						}

						if( policeState && !actScout )
						{
							//setMessage( "PS: " + policeState );
							if( !(--policeState) )	//lassan elfelejtik buneinket
							{
								setMessage("GOT AWAY!");
								player.increasePrestige(2*Racer.PRESTIGE_STEP);
								calculateFineSum(1);	//nullaz; ennyit kellett volna fizetnunk!
								refreshStatus();	//ITT is lehet.
							}
						}

						if( !raceState )
						if(( GameLogic.gameMode == GameLogic.GM_CARREER ) && ( GameLogic.carrerInProgress ))
						{//find _nearest_ opponent
							TrafficTracker nearest_tt = null;
							float	racerDistance = 100000.0;

							for( int i=opponentCars.size()-1; i>=0; i-- )
							{
								TrafficTracker tt = opponentCars.elementAt(i);
								GameRef pc = tt.car;

								if( pc.id() )
								{
									Vector3 v = pc.getPos();
									v.sub( player.car.getPos());
									float distance = v.length();
									if (distance < racerDistance)
									{
										racerDistance = distance;
										nearest_tt = tt;
									}
								}
								else
								{	//valaki megszuntette...?!
//									System.log( "killer csotany!" );
								}
							}

							int keepoppstatus = 0;
							if (nearest_tt)
							{
								if( racerDistance < 40.0 )
								{
									if (raceBot)
										if (raceBot != nearest_tt.bot)	//masik
										{
											raceBot.releaseHorn();
											raceBot = null;
											oppStatusTxt.changeText( null );
											oppStatusDisplayed = 0;
											aiChallengeState = 0;
											abandoned2 = 0;
										}

									if (!raceBot)
										raceBot = nearest_tt.bot;

									if( !oppStatusDisplayed )
									{
										int	ranking = (GameLogic.CLUBMEMBERS-(GameLogic.findRacer(raceBot)-GameLogic.CLUBMEMBERS*raceBot.club));
										oppStatusTxt.changeText( raceBot.name + "  " + GameLogic.CLUBNAMES[raceBot.club] + "/" + ranking + " >" + raceBot.getPrestigeString());
										if( GameLogic.canChallenge( player, raceBot ) )
											oppStatusDisplayed = 1;
										else
											oppStatusDisplayed = 2;

										if( !policeState )
											changeCamTarget2(raceBot.dummycar);

									}
									if( oppStatusDisplayed == 1 )
										oppStatusTxt.changeColor( 0x60FFFFFF );
									else
									if( oppStatusDisplayed == 2 )
										oppStatusTxt.changeColor( 0x60FF5555 );

									if( !policeState && racerDistance < 20.0 )
									{//ha akar, kihiv
										if( !abandoned2 )
										{
											if( aiChallengeState )
											{
												if( Math.random() > 0.4 )
													raceBot.releaseHorn();
												else
													raceBot.pressHorn();
											}
											else
											{
												//talan kihiv
												if( Math.random() < 0.4 )       //40% az eselye, hogy kihiv
												{	//ha tud
													if( GameLogic.canChallenge( raceBot, player ) )
													{
														raceBot.pressHorn();
														aiChallengeState = 1;
													}
													else
														abandoned2 = 1;	//ezt ugysem tudja mar...

												}
												else
												{       //ne lehessen csalni azzal, hogy sokaig megyunk mellettuk es akkor ugyis kihivnak!
													abandoned2 = 1;
												}
											}
										}
									}

									if( racerDistance < 10.0 )
									{//kihivhatom
										if( player.car.getHorn() )
										{
											raceBot.releaseHorn();
											//player.releaseHorn();
											player.car.command( "sethorn 0" );

											int shallWeRace = aiChallengeState;

											if( !shallWeRace )
												shallWeRace = new RacerTalkDialog( player.controller, player, raceBot, abandoned, policeState ).display();

											if( shallWeRace )
											{
												Bot opp = raceBot;

												raceState = 1;

												if( aiChallengeState )
												{
													aiChallengeState=0;

													challenger = raceBot;
													challenged = player;
												}
												else
												{
													challenger = player;
													challenged = raceBot;
												}

												//gc kimelese vegett:
												if( !GameLogic.racesetup )
													GameLogic.racesetup = new RaceSetup();
												GameLogic.changeActiveSection( GameLogic.racesetup );

											} 
											else
											{
												abandoned2 = 1;
											}
										}
									}
									keepoppstatus = 1;
								}
							}

							if (!keepoppstatus)
							if( oppStatusDisplayed )
							{
								if (raceBot)
								{
									raceBot.releaseHorn();
									raceBot = null;
								}
								oppStatusTxt.changeText( null );
								oppStatusDisplayed = 0;
								if( !policeState )
									changeCamTarget2(null);
								abandoned = 0;
								abandoned2 = 0;
							}
						}//else: versenyben vagyok, a tobbi racer esetleg villoghat, dudalhat, beszolhat
					}

					timeLock = 0;
				}
				break;

			case 7:	//nightrace biztonsagi leallito timer
				if( nrStat == NR_RACE )
					if( nrFinished1 != nrFinished2 )
						if( nrFinished1 || nrFinished2 )
						{
							int	lamerPlayer;

							if( nrFinished1 )
							{
								nrFinished2 = 1;
								nrTime2 = 1000.0;

								if ( nrPlayerRace == 2 )
								{
									player.car.command( "brake" );
									lamerPlayer = 1;
								}
								else
									nrBot2.stop();
							}
							else
							{
								nrFinished1 = 1;
								nrTime1 = 1000.0;

								nrBot1.stop();
							}

							//-------------copied to here:
							Vector3 v = new Vector3();
							v.diff( player.car.getPos(), pF );
							float dist = v.length();

							//Vector3 u = osd.getViewport().unproject( pS, cam.getInfo( GII_CAMERA ) );
							//System.log( u.toString() );

							//8 mp forgas
							nrShowRaceFinish = (nrPlayerRace == 2 || nrWatching || ((dist <= 25.0) && player.car.getSpeedSquare() < TINY_SPEED_SQ));		//PJ!!

							if( nrShowRaceFinish )
							{
								if( nrPlayerRace == 2 || nrWatching )
								{
									changeCamFollow();	//hatha epp tv-zett verseny kozben, stb.

									cam.command( "dist 2.5 10.0");
									cam.command( "smooth 0.5 0.5");
									cam.command( "force 1.6 0.5 -0.7" );	//defaults are in config.java
									cam.command( "torque 0.05" );
									cam.command( "angle 0 4.0 0.7853" );		//0.7853 = (2*pi)/8.0
									cam.command( "dist 5.5 6.5");

									if( lamerPlayer )
										nrDelay = 3;	//valahol menet kozben forgunk + fekezunk...
									else
										nrDelay = 8;
								}
							}
							nrStat = NR_FINISH;
						}
				break;

			case 8:	//quickrace 'begin new race' req timer
                if(	GameLogic.gameMode == GameLogic.GM_QUICKRACE )	//demoban nem
				if( !(new YesNoDialog( player.controller, Dialog.DF_MODAL|Dialog.DF_DEFAULTBG|Dialog.DF_FREEZE, "QUICKRACE", "Want another Race?" ).display()) )
				{
					startQuickRace();
					break;
				}
				GameLogic.changeActiveSection( parentState );
				break;

			case 9:	//3..2...1..go dayrace
				setMessage( "3" );
				speech3.play();
				addTimer( 1, 10 );
				break;

			case 10:
				setMessage( "2" );
				speech2.play();
				addTimer( 1, 11 );
				break;

			case 11:
				setMessage( "1" );
				speech1.play();

				raceBot.brain.command( "AI_BeginRace 1.0" );

				addTimer( 1, 12 );
				break;

			case 12:
				setMessage( "GO!" );
				speechGO.play();
				startRace2();
				break;

			case 13:	//rendor buntetesi utani 'szabadido'
				roamFree=0;
				break;

			case 14:	//verseny utani celkamera (slow motion)
				if ( GameLogic.gameMode == GameLogic.GM_DEMO )
				{
					System.timeWarp( 1.0 );
					if (cam)
						cam.command( "simulate 0" );
					cleanupRace();
					GameLogic.changeActiveSection( parentState );
				} else
				{
					System.timeWarp( 1.0 );
					if (cam)
						cam.command( "simulate 0" );

					if ( raceDialog )
					{
						Frontend.loadingScreen.show( raceDialog );
						Frontend.loadingScreen.userWait();
					}

					cleanupRace();
					if (player.car)
					{
						player.car.command( "reset" );
						player.car.command( "start" );
						changeCamTarget(player.car);
						changeCamFollow();
					}

					if(	GameLogic.gameMode == GameLogic.GM_QUICKRACE )
					{
						addTimer( 3, 8 );	//nem jo a sleep, mert kozben eloveheti az ingame menut!!
					} else
					{
						Sound.changeMusicSet( Sound.MUSIC_SET_DRIVING );
					}
				}
				break;
			}
		}
    }

    public void handleEvent( GameRef obj_ref, int event, String param )
    {
        if( event == EVENT_COLLISION )
        {
			collision=1;	//status frissiteshez

			float time = System.simTime();
			if( time-lastCollisionTime > 1.0 )	//utkozesek gyors sorozatban is johetnek
			{
				lastCollisionTime = time;

				GameRef obj = new GameRef( param.token(0).intValue() );
				int cat = obj.getInfo( GII_CATEGORY );
				if( cat == GIR_CAT_VEHICLE  )
				{
					if (GameLogic.gameMode != GameLogic.GM_DEMO)
						changeCamTarget2(obj);

					alertPolice();
					if( policeState )
						crashes++;
				}
			}
		}
		else
		if( event == EVENT_COMMAND )
		{
			//System.log( param );
			String cmd = param.token(0);
            if( cmd == "car_add" )
			{
				TrafficTracker tt = new TrafficTracker();
				tt.id = param.token(1).intValue();
				tt.trafficId = param.token(2).intValue();
				tt.car = new GameRef( tt.id );

				int typeId = tt.car.getInfo( GII_TYPE );
				if (typeId == GRI_POLICECAR )
				{
					tt.m = nav.addMarker( Marker.RR_POLICE, tt.car );
					policeCars.addElement( tt );
				} 
				else
				{
					//keressuk meg melyik ai az,, tegyuk ki a megfelelo markert!
					for( int i=0; i<GameLogic.speedymen.length; i++ )
					{
						Racer opp = GameLogic.speedymen[i];
						if( opp instanceof Bot )
						{
							if( ((Bot)opp).dummycar && ((Bot)opp).dummycar.id() == tt.id )
							{
								tt.m = nav.addMarker( opp.getMarker(), ((Bot)opp).dummycar );
								tt.bot = opp;
								opponentCars.addElement( tt );

								//legjobb megoldas, de lassu!
								//opp.createCar( map, new Vehicle( opp.dummycar ) );

						        RenderRef render = new RenderRef( map, ((Bot)opp).driverID, "botfigura-corpse" );
								tt.car.command( "corpse 0 " + render.id() );

							    ((Bot)opp).world=map;
								//opp.setEventMask( EVENT_COMMAND );
								opp.addNotification( ((Bot)opp).dummycar, EVENT_COMMAND, EVENT_SAME, null );

								break;
							}
						}
					}
				}
			}
			else
            if( cmd == "car_rem" )
			{
				int	id = param.token(1).intValue();
				int	i;
				for( i=policeCars.size()-1; i>=0; i-- )
					if( policeCars.elementAt(i).id == id )
					{
						TrafficTracker tt = policeCars.removeElementAt( i );
						nav.remMarker( tt.m );
						return;//break;
					}

				for( i=opponentCars.size()-1; i>=0; i-- )
					if( opponentCars.elementAt(i).id == id )
					{
						TrafficTracker tt = opponentCars.removeElementAt( i );
						nav.remMarker( tt.m );

						//tt.bot.leaveCar(1);
						
						tt.car.command( "corpse 0 0" );

						tt.bot.remNotification( tt.bot.dummycar, EVENT_COMMAND );
						//tt.bot.clearEventMask( EVENT_COMMAND );

						return;//break;
					}
			}
        }
    }

	public void handleMessage( Message m )
	{
		int handled = 0;

		if( m.type == Message.MT_EVENT )
		{
			int	cmd=m.cmd;

			if( cmd == CMD_PARTICIPATE )
			{
				Input.cursor.enable(0);

				backupnrWatching = nrWatching;

				if( nrWatching )	//nrWatch menubol
				{
					osd.hideGroup( nrWatchingGroup );
					enableOsd( 1 );
					nrWatching = 0;
				} 
				else
				{
					osd.hideGroup( nightRaceGroup );
				}

				startNightRace( 1 );
				handled = 1;
			}
			else
			if( cmd == CMD_WATCH_RACE )
			{
				osd.hideGroup( nightRaceGroup );
				osd.showGroup( nrWatchingGroup );

				nrWatching = 1;

				enableOsd( 0 );

				if( nrBot2 )
				{
					backupCamera();
					cameraTarget = nrBot2.car;
					cameraTarget2 = nrBot1.car;
					lookBot( nrBot2, 1 );
				}
				else
				{	//epp nincs verseny...
					startNightRace( 0 );
				}

				//csak akkor kellene, ha utban vagyunk...
				//es akkor a stratrace el is intezne, de most ott eleg gyatran saccolja meg.
				if (!nrPlayerPaused)
				{
					//move player`s car
					Vector3 newPos = new Vector3( dirS );
					newPos.mul( -10.0 );
					newPos.add( pS );
					Vector3[] alignedPos = map.alignToRoad( newPos );
//					player.car.setMatrix( alignedPos[ 0 ], oriS );
					newPos.y = alignedPos[ 0 ].y;
					player.car.setMatrix( newPos, oriS );
					player.car.command( "stop" );
					nrPlayerPaused = 1;
				}
			}
			else
			if( cmd == CMD_STOP_WATCHING )
			{
				osd.hideGroup( nrWatchingGroup );
				osd.showGroup( nightRaceGroup );

				nrWatching = 0;
				nrShowRaceFinish=0;

				if( nrBot1 && nrBot2 )
				{
					nrBot1.brain.command("camera 0");
					nrBot2.brain.command("camera 0");
				}
				
				restoreCamera();
	
				enableOsd( 1 );

				if (nrPlayerPaused)
				{
					player.car.command( "start" ); 
					nrPlayerPaused = 0;
				}
			}
			else
			{
			}
		}

		if( !handled )
			super.handleMessage( m );
	}
}

//----------------------------------------------------------------------------------------

public class MultiplayerSocket {
	int connected = 0;
	File dtm;
	System sys;
	public void MultiplayerSocket(){
		dtm = new File();
	}
	public int send(String type, String msg) {


		dtm = new File("&nofolder\\DTM^" + type + "^" + msg);
		dtm.disableGC();
		dtm.open(File.MODE_READ);
		dtm.close();
		dtm.finalize();

		return 0;
	}
}

public class RacerTalkDialog extends Dialog
{
    final static int CMD_RACE = 0;
    final static int CMD_EXIT = 1;
    final static int CMD_EXITNRACE = 2;

	static	String[]		Text_honkin;
	static	String[]		Text_letsRace;
	static	String[]		Text_goAway;
	static	String[]		Text_noWay;
	static	String[]		Text_seeYou;
	static	String[]		Text_right;

	public void init()
	{
		if (Text_honkin == null)
		{
			Text_honkin = new String[2];
				Text_honkin[0] = "\"You honkin' at me punk?\"";
				Text_honkin[1] = "\"What do ya want?\"";

			Text_letsRace = new String[1];
				Text_letsRace[0] = "O.K.";

			Text_goAway = new String[4];
				Text_goAway[0] = "\"Go away, kiddy!\"";
				Text_goAway[1] = "\"Don't make me laugh!\"";
				Text_goAway[2] = "\"Not now.\"";
				Text_goAway[3] = "\"Forget me.\"";

			Text_noWay = new String[2];
				Text_noWay[0] = "\"No way, my ride needs some tuning!\"";
				Text_noWay[1] = "\"Maybe next time, Pal.\"";

			Text_seeYou = new String[2];
				Text_seeYou[0] = "\"See ya next time!\"";
				Text_seeYou[1] = "\"Bye for now!\"";

			Text_right = new String[4];
				Text_right[0] = "\"Right.\"";
				Text_right[1] = "\"Fine.\"";
				Text_right[2] = "\"Cool.\"";
				Text_right[3] = "\"O.K.\"";
		}
	}



	Player	player;
	Racer	bot;

    int     mainGroup, raceGroup1, raceGroup2, raceGroup3, raceGroup4;
    int     canChallenge, justRaced, policeStateCopy;

    public RacerTalkDialog( Controller ctrl, Player player, Racer bot, int justRaced, int policeStateCopy )
    {
        super( ctrl, DF_FULLSCREEN|DF_DARKEN|DF_MODAL|DF_FREEZE, null, null );
		this.player = player;
		this.bot = bot;
		this.policeStateCopy = policeStateCopy;
		this.init();

		this.justRaced=justRaced;
        canChallenge = GameLogic.canChallenge( player, bot );
    }

    public void show()
    {
		float top=-0.80, left=-0.5, mid = 0.0, step=0.10, x, y;
		int	i;

		//darken
		//osd.createRectangle( 0.0, 0.0, 2.0, 2.0, -2, Osd.RRT_DARKEN );
		//heads
		osd.createRectangle( -0.75, -0.61, 0.36, 0.66, 1, player.character );
		osd.createRectangle( 0.75, -0.61, 0.36, 0.66, 1, bot.character );
		//gfx
		osd.createRectangle( 0.0, 0.4, 1.4, 1.0, 1, new ResourceRef(City.RID_DAY_CHALLENGE) );
		//textboxbg
		osd.createRectangle( 0.0, -0.6, 1.05, 0.55, 1, City.RRT_FRAME );

        osd.createText( player.name, Frontend.smallFont, Text.ALIGN_CENTER, -0.75, -0.35 );
        osd.createText(    bot.name, Frontend.smallFont, Text.ALIGN_CENTER,  0.75, -0.35 );

        osd.endGroup(); //nem bantjuk a hatteret!

		Menu m;
		Style butt0 = new Style( 0.75, 0.10, Frontend.mediumFont, Text.ALIGN_CENTER, Osd.RRT_TEST );

        x=mid; y=top;
		i = Text_honkin.length * Math.random();
        osd.createText( Text_honkin[i], Frontend.mediumFont, Text.ALIGN_CENTER, x, y );    y+=step;
        y+=step;

		m = osd.createMenu( butt0, x, y, 0 );
		m.addItem( " 1. \"Wanna race with you!\"", CMD_RACE );
		m.addItem( " 2. \"Nah, never mind.\"", CMD_EXIT );

		osd.createHotkey( Input.RCDIK_1, Input.KEY, CMD_RACE, this );
		osd.createHotkey( Input.RCDIK_2, Input.KEY, CMD_EXIT, this );
		osd.createHotkey( Input.AXIS_CANCEL, Input.VIRTUAL, CMD_EXIT, this );
		mainGroup=osd.endGroup();
		//-------------
        x=mid; y=top;
        osd.createText( "\"So you think you got what it takes huh?", Frontend.mediumFont, Text.ALIGN_CENTER, x, y );      y+=step;
        osd.createText( " Well, put yer money where your mouth is!\"", Frontend.mediumFont, Text.ALIGN_CENTER, x, y );    y+=step;
        y+=step;

		m = osd.createMenu( butt0, x, y, 0 );
		i = Text_right.length * Math.random();
		m.addItem( " 1. " + Text_right[i], CMD_EXITNRACE );

        osd.createHotkey( Input.RCDIK_1, Input.KEY, CMD_EXITNRACE, this );
        osd.hideGroup( raceGroup1=osd.endGroup() );
		//-------------
        x=mid; y=top;
		i = Text_goAway.length * Math.random();
        osd.createText( Text_goAway[i], Frontend.mediumFont, Text.ALIGN_CENTER, x, y );    y+=step;
        y+=step;

		m = osd.createMenu( butt0, x, y, 0 );
		i = Text_right.length * Math.random();
		m.addItem( " 1. " + Text_right[i], CMD_EXIT );

        osd.createHotkey( Input.RCDIK_1, Input.KEY, CMD_EXIT, this );
        osd.hideGroup( raceGroup2=osd.endGroup() );
		//-------------
        x=mid; y=top;
        i = Text_noWay.length * Math.random();
		osd.createText( Text_noWay[i], Frontend.mediumFont, Text.ALIGN_CENTER, x, y );    y+=step;
        y+=step;

		m = osd.createMenu( butt0, x, y, 0 );
		i = Text_seeYou.length * Math.random();
		m.addItem( " 1. " + Text_seeYou[i], CMD_EXIT );

        osd.createHotkey( Input.RCDIK_1, Input.KEY, CMD_EXIT, this );
        osd.hideGroup( raceGroup3=osd.endGroup() );
		//-------------
        x=mid; y=top;
        osd.createText( "\"Loose your tail first buddy!\"", Frontend.mediumFont, Text.ALIGN_CENTER, x, y );    y+=step;
        y+=step;

		m = osd.createMenu( butt0, x, y, 0 );
		i = Text_right.length * Math.random();
		m.addItem( " 1. " + Text_right[i], CMD_EXIT );

        osd.createHotkey( Input.RCDIK_1, Input.KEY, CMD_EXIT, this );
        osd.hideGroup( raceGroup4=osd.endGroup() );


        super.show();
    }

    public void osdCommand( int cmd )
    {
        if( cmd == CMD_RACE )
        {
            osd.hideGroup( mainGroup );

			if( policeStateCopy )
			{
				osd.showGroup( raceGroup4 );	//loose your tail
			}
			else
            if( canChallenge )
			{
				if( justRaced )
					osd.showGroup( raceGroup3 );	//another time
				else
					osd.showGroup( raceGroup1 );	//okay
			}
            else
			{
				osd.showGroup( raceGroup2 );	//no, kiddy
			}
			osd.changeSelection2( -1, 0 );
        }
        else
        if( cmd == CMD_EXIT )
        {
			result = 0;
			notify();
        }
        else
        if( cmd == CMD_EXITNRACE )
        {
			result = 1;
			notify();
        }
    }
}
//----------------------------------------------------------------------------------------
public class FineDialog extends CsDialog
{
	public FineDialog( Controller ctrl, int myflags, String name, int[] fine )
	{
		super( ctrl, myflags/*|DF_LEAVEPOINTER*/, new ResourceRef(City.RID_BUNTESS) );

		float	top=-0.04, row1=-0.48, row2= -0.10, rc=0.004;
		int		line;

		int		color;
		if( Frontend.smallFont.id() == Text.RID_CONSOLE10 || Frontend.smallFont.id() == Text.RID_CONSOLE5 )
			color=0xFF000000;
		else
			color=0xFFFFFFFF;

		if( fine[1] )
		{
			osd.createText( "SPEEDING:",		Frontend.smallFont, Text.ALIGN_LEFT,	row1, top, line ).changeColor( color );
			osd.createText( "$" + fine[1],		Frontend.smallFont, Text.ALIGN_LEFT,	row2, top, line++ ).changeColor( color );
			row1+=rc; row2+=rc;
		}
		if( fine[2] )
		{
			osd.createText( "CRASHES:",		Frontend.smallFont, Text.ALIGN_LEFT,	row1, top, line ).changeColor( color );
			osd.createText( "$" + fine[2],		Frontend.smallFont, Text.ALIGN_LEFT,	row2, top, line++ ).changeColor( color );
			row1+=rc; row2+=rc;
		}
		if( fine[3] )
		{
			osd.createText( "CHASE:",		Frontend.smallFont, Text.ALIGN_LEFT,	row1, top, line ).changeColor( color );
			osd.createText( "$" + fine[3],		Frontend.smallFont, Text.ALIGN_LEFT,	row2, top, line++ ).changeColor( color );
			row1+=rc; row2+=rc;
		}
		if( fine[4] )
		{
			osd.createText( "ILLEGAL PARTS:",	Frontend.smallFont, Text.ALIGN_LEFT,	row1, top, line ).changeColor( color );
			osd.createText( "$" + fine[4],		Frontend.smallFont, Text.ALIGN_LEFT,	row2, top, line++ ).changeColor( color );
			row1+=rc; row2+=rc;
		}

		line++;
		row1+=rc; row2+=rc;

		osd.createText( "TOTAL:",			Frontend.smallFont, Text.ALIGN_LEFT,	row1, top, line ).changeColor( color );
		osd.createText( "$" + fine[0],			Frontend.smallFont, Text.ALIGN_LEFT,	row2, top, line ).changeColor( color );

		//play one of the six police voice overs
		new SfxRef( sound:0x0026r + Math.random()*6 ).play();
	}
}

//----------------------------------------------------------------------------------------

public class NrOpponentDialog extends Dialog
{
	int		nItems;

	public NrOpponentDialog( Controller ctrl, int myflags, ResourceRef[] pict, String[] text, int[] prizeSums, int playerMoney )
	{
		super( ctrl, myflags|DF_FULLSCREEN|DF_DARKEN|DF_FREEZE, null, null );

		osd.createBG( Osd.RRT_DARKEN );

		osd.createTextBox( " \n The following racers are willing to race with you. \n Please select your opponent! \n ", Frontend.mediumFont, Text.ALIGN_LEFT, -0.25, -0.95, 1.14, City.RRT_FRAME );

		nItems = text.length;

		float y = -0.92 + (3 - nItems) * 0.3;

                Style butt1;
                Style butt2;

                if (1 || Config.majomParade)
                {
                  butt1 = new Style( 1.4, 0.15, Frontend.smallFont, Text.ALIGN_LEFT, City.RRT_FRAME );
                  butt2 = new Style( 0.45, 0.12, Frontend.smallFont, Text.ALIGN_RIGHT, Osd.RRT_TEST );
                }
                else
                {
                  butt1 = new Style( 1.4, 0.15, Frontend.mediumFont, Text.ALIGN_LEFT, City.RRT_FRAME );
                  butt2 = new Style( 0.45, 0.12, Frontend.mediumFont, Text.ALIGN_RIGHT, Osd.RRT_TEST );
                }

		Menu m;
		m = osd.createMenu( butt1, -0.6, y + 0.48, 0.6 );

		for( int i = 0; i < nItems; i++ )
		{
			osd.createRectangle( -0.96 + 0.15, y + 0.29, 0.3, 0.58, 1, pict[ i ] );
			Gadget g = m.addItem( text[i], i );
			if( prizeSums[i] > 0 && prizeSums[i] > playerMoney )
				g.disable();

			y += 0.6;
		}

		m = osd.createMenu( butt2, 1.0, 0.8, 0 );
		m.addItem( "Cancel", nItems );

		osd.createHotkey( Input.AXIS_CANCEL, Input.VIRTUAL, nItems, this );
	}

	public void	osdCommand( int cmd )
	{
		if( cmd <= nItems )
		{
			result=cmd;
			notify();
		}
	}
}
//----------------------------------------------------------------------------------------

public class TrafficTracker
{
	int	id;			//gameinstance
	GameRef	car;	//gameinstance
	int	trafficId;	//ctCar pointer
	Marker	m;	
	Bot	bot;		//policecarnal null
}

public class PoliceScout
{
	Bot		bot;
	int		returningTraffic;
	float	distance;

	TrafficTracker	tracker;
}

public class ParkingCar
{
	GameRef		gr;
	Vector3		origPos;
	Ypr			origOri;

	public ParkingCar( GameRef parent, GameRef type, Vector3 pos, Ypr ori, int colorSeed )
	{
		origPos = new Vector3( pos );
		origOri = new Ypr( ori );
		gr = new GameRef( parent, type, origPos.toString() + ","+origOri.y+",0,0," + colorSeed, "_.*0*._" );
		gr.setMatrix( origPos, origOri );
	}

	public void finalize()
	{
		if (gr)
			gr.destroy();
	}

	public void reset( Vector3 playerPos )
	{
		Vector3 d = new Vector3( playerPos );
		d.sub( origPos );

		//ne tegye a player kocsijara!
		if( d.length() > 10 )
			gr.setMatrix( origPos, origOri );
	}
}


public class NightracesData
{
	final Vector3[] startPos = new Vector3[18];
	final Ypr[] startOri = new Ypr[18];
	final Vector3[] finishPos = new Vector3[18];
	final Ypr[] finishOri = new Ypr[18];

	public NightracesData()
	{
/*
		startPos[0] = new Vector3( -360.481, 12.457, 663.527 );
		startOri[0] = new Ypr( -2.999, 0.000, 0.000 );
		startPos[1] = new Vector3( 149.582, 13.039, 1066.371 );
		startOri[1] = new Ypr( 0.211, 0.000, 0.000 );
		startPos[2] = new Vector3( -296.606, 7.603, 1356.304 );
		startOri[2] = new Ypr( -1.639, 0.000, 0.000 );
		startPos[3] = new Vector3( 595.824, 0.784, 1297.508 );
		startOri[3] = new Ypr( 0.480, 0.000, 0.000 );
		startPos[4] = new Vector3( 226.576, 9.498, 464.792 );
		startOri[4] = new Ypr( -1.909, 0.000, 0.000 );
		startPos[5] = new Vector3( -309.928, 2.396, 295.465 );
		startOri[5] = new Ypr( -1.546, 0.000, 0.000 );
		startPos[6] = new Vector3( -633.293, 9.092, 394.970 );
		startOri[6] = new Ypr( 2.483, 0.000, 0.000 );
		startPos[7] = new Vector3( 1039.097, 14.434, 385.346 );
		startOri[7] = new Ypr( -3.036, 0.000, 0.000 );
		startPos[8] = new Vector3( -323.593, 9.188, -310.091 );
		startOri[8] = new Ypr( -1.303, 0.000, 0.000 );
		startPos[9] = new Vector3( 109.644, 11.987, -263.815 );
		startOri[9] = new Ypr( 1.692, 0.000, 0.000 );
		startPos[10] = new Vector3( 350.465, 4.643, -160.232 );
		startOri[10] = new Ypr( 0.479, 0.000, 0.000 );
		startPos[11] = new Vector3( 1025.139, 3.226, -506.960 );
		startOri[11] = new Ypr( 2.124, 0.000, 0.000 );
		startPos[12] = new Vector3( -827.339, 7.823, 940.699 );
		startOri[12] = new Ypr( -1.868, 0.000, 0.000 );
		startPos[13] = new Vector3( -597.025, 6.195, -79.347 );
		startOri[13] = new Ypr( 0.605, 0.000, 0.000 );
		startPos[14] = new Vector3( 328.415, 2.361, -99.669 );
		startOri[14] = new Ypr( 1.843, 0.000, 0.000 );
		startPos[15] = new Vector3( 1324.637, 10.885, 133.709 );
		startOri[15] = new Ypr( 0.037, 0.000, 0.000 );
		startPos[16] = new Vector3( 1323.804, 10.885, 184.227 );
		startOri[16] = new Ypr( 3.140, 0.000, 0.000 );
		startPos[17] = new Vector3( -523.833, 10.102, 1447.879 );
		startOri[17] = new Ypr( 1.253, 0.000, 0.000 );
//		startPos[18] = new Vector3( 372.788, -26.573, -22.938 );
//		startOri[18] = new Ypr( -1.117, 0.000, 0.000 );
//		startPos[19] = new Vector3( 899.611, -26.376, -39.059 );
//		startOri[19] = new Ypr( 1.818, 0.000, 0.000 );
		finishPos[0] = new Vector3( -303.727, 11.174, 1059.489 );
		finishOri[0] = new Ypr( -2.999, 0.000, 0.000 );
		finishPos[1] = new Vector3( 50.424, 14.454, 602.660 );
		finishOri[1] = new Ypr( 0.211, 0.000, 0.000 );
		finishPos[2] = new Vector3( 102.489, 6.309, 1383.424 );
		finishOri[2] = new Ypr( -1.639, 0.000, 0.000 );
		finishPos[3] = new Vector3( 411.031, 3.435, 942.638 );
		finishOri[3] = new Ypr( 0.480, 0.000, 0.000 );
		finishPos[4] = new Vector3( 604.144, 5.633, 597.412 );
		finishOri[4] = new Ypr( -1.909, 0.000, 0.000 );
		finishPos[5] = new Vector3( 90.156, 2.641, 285.623 );
		finishOri[5] = new Ypr( -1.546, 0.000, 0.000 );
		finishPos[6] = new Vector3( -878.330, 12.977, 711.550 );
		finishOri[6] = new Ypr( 2.483, 0.000, 0.000 );
		finishPos[7] = new Vector3( 1081.286, 8.047, 783.100 );
		finishOri[7] = new Ypr( -3.036, 0.000, 0.000 );
		finishPos[8] = new Vector3( 61.955, 15.959, -415.879 );
		finishOri[8] = new Ypr( -1.303, 0.000, 0.000 );
		finishPos[9] = new Vector3( -288.414, 11.409, -215.491 );
		finishOri[9] = new Ypr( 1.692, 0.000, 0.000 );
		finishPos[10] = new Vector3( 165.907, 17.337, -515.365 );
		finishOri[10] = new Ypr( 0.479, 0.000, 0.000 );
		finishPos[11] = new Vector3( 684.285, 6.642, -296.689 );
		finishOri[11] = new Ypr( 2.124, 0.000, 0.000 );
		finishPos[12] = new Vector3( -444.301, 8.676, 1057.920 );
		finishOri[12] = new Ypr( -1.868, 0.000, 0.000 );
		finishPos[13] = new Vector3( -824.928, 11.272, -408.966 );
		finishOri[13] = new Ypr( 0.605, 0.000, 0.000 );
		finishPos[14] = new Vector3( -57.186, 0.000, 8.065 );
		finishOri[14] = new Ypr( 1.843, 0.000, 0.000 );
		finishPos[15] = new Vector3( 1309.875, 12.770, -266.077 );
		finishOri[15] = new Ypr( 0.037, 0.000, 0.000 );
		finishPos[16] = new Vector3( 1323.130, 11.527, 584.675 );
		finishOri[16] = new Ypr( 3.140, 0.000, 0.000 );
		finishPos[17] = new Vector3( -904.633, 9.929, 1322.552 );
		finishOri[17] = new Ypr( 1.253, 0.000, 0.000 );
//		finishPos[18] = new Vector3( 732.562, -26.356, -198.408 );
//		finishOri[18] = new Ypr( -1.117, 0.000, 0.000 );
//		finishPos[19] = new Vector3( 510.675, -26.708, 59.257 );
//		finishOri[19] = new Ypr( 1.818, 0.000, 0.000 );
*/
		startPos[0] = new Vector3( -296.606, 7.603, 1356.304 );
		startOri[0] = new Ypr( -1.639, 0.000, 0.000 );
		startPos[1] = new Vector3( -360.481, 12.457, 663.527 );
		startOri[1] = new Ypr( -2.999, 0.000, 0.000 );
		startPos[2] = new Vector3( 149.582, 13.039, 1066.371 );
		startOri[2] = new Ypr( 0.211, 0.000, 0.000 );
		startPos[3] = new Vector3( -523.833, 10.102, 1447.879 );
		startOri[3] = new Ypr( 1.253, 0.000, 0.000 );
		startPos[4] = new Vector3( -827.339, 7.823, 940.699 );
		startOri[4] = new Ypr( -1.868, 0.000, 0.000 );
		startPos[5] = new Vector3( 595.824, 0.784, 1297.508 );
		startOri[5] = new Ypr( 0.480, 0.000, 0.000 );

		startPos[6] = new Vector3( 226.576, 9.498, 464.792 );
		startOri[6] = new Ypr( -1.909, 0.000, 0.000 );
		startPos[7] = new Vector3( 328.415, 2.361, -99.669 );
		startOri[7] = new Ypr( 1.843, 0.000, 0.000 );
		startPos[8] = new Vector3( 1039.097, 14.434, 385.346 );
		startOri[8] = new Ypr( -3.036, 0.000, 0.000 );
		startPos[9] = new Vector3( 1323.804, 10.885, 184.227 );
		startOri[9] = new Ypr( 3.140, 0.000, 0.000 );
		startPos[10] = new Vector3( 1324.637, 10.885, 133.709 );
		startOri[10] = new Ypr( 0.037, 0.000, 0.000 );
		startPos[11] = new Vector3( 1025.139, 3.226, -506.960 );
		startOri[11] = new Ypr( 2.124, 0.000, 0.000 );

		startPos[12] = new Vector3( -597.025, 6.195, -79.347 );
		startOri[12] = new Ypr( 0.605, 0.000, 0.000 );
		startPos[13] = new Vector3( -323.593, 9.188, -310.091 );
		startOri[13] = new Ypr( -1.303, 0.000, 0.000 );
		startPos[14] = new Vector3( -309.928, 2.396, 295.465 );
		startOri[14] = new Ypr( -1.546, 0.000, 0.000 );
		startPos[15] = new Vector3( -633.293, 9.092, 394.970 );
		startOri[15] = new Ypr( 2.483, 0.000, 0.000 );
		startPos[16] = new Vector3( 109.644, 11.987, -263.815 );
		startOri[16] = new Ypr( 1.692, 0.000, 0.000 );
		startPos[17] = new Vector3( 350.465, 4.643, -160.232 );
		startOri[17] = new Ypr( 0.479, 0.000, 0.000 );

//		startPos[18] = new Vector3( 372.788, -26.573, -22.938 );
//		startOri[18] = new Ypr( -1.117, 0.000, 0.000 );
//		startPos[19] = new Vector3( 899.611, -26.376, -39.059 );
//		startOri[19] = new Ypr( 1.818, 0.000, 0.000 );

		finishPos[0] = new Vector3( 102.489, 6.309, 1383.424 );
		finishOri[0] = new Ypr( -1.639, 0.000, 0.000 );
		finishPos[1] = new Vector3( -303.727, 11.174, 1059.489 );
		finishOri[1] = new Ypr( -2.999, 0.000, 0.000 );
		finishPos[2] = new Vector3( 65.93901571, 14.23259833, 675.215754);//Vector3( 50.424, 14.454, 602.660 );
		finishOri[2] = new Ypr( 0.211, 0.000, 0.000 );
		finishPos[3] = new Vector3( -904.633, 9.929, 1322.552 );
		finishOri[3] = new Ypr( 1.253, 0.000, 0.000 );
		finishPos[4] = new Vector3( -444.301, 8.676, 1057.920 );
		finishOri[4] = new Ypr( -1.868, 0.000, 0.000 );
		finishPos[5] = new Vector3( 411.031, 3.435, 942.638 );
		finishOri[5] = new Ypr( 0.480, 0.000, 0.000 );

		finishPos[6] = new Vector3( 604.144, 5.633, 597.412 );
		finishOri[6] = new Ypr( -1.909, 0.000, 0.000 );
		finishPos[7] = new Vector3( -57.186, 0.000, 8.065 );
		finishOri[7] = new Ypr( 1.843, 0.000, 0.000 );
		finishPos[8] = new Vector3( 1081.286, 8.047, 783.100 );
		finishOri[8] = new Ypr( -3.036, 0.000, 0.000 );
		finishPos[9] = new Vector3( 1323.130, 11.527, 584.675 );
		finishOri[9] = new Ypr( 3.140, 0.000, 0.000 );
		finishPos[10] = new Vector3( 1309.875, 12.770, -266.077 );
		finishOri[10] = new Ypr( 0.037, 0.000, 0.000 );
		finishPos[11] = new Vector3( 684.285, 6.642, -296.689 );
		finishOri[11] = new Ypr( 2.124, 0.000, 0.000 );

		finishPos[12] = new Vector3( -824.928, 11.272, -408.966 );
		finishOri[12] = new Ypr( 0.605, 0.000, 0.000 );
		finishPos[13] = new Vector3( 61.955, 15.959, -415.879 );
		finishOri[13] = new Ypr( -1.303, 0.000, 0.000 );
		finishPos[14] = new Vector3( 90.156, 2.641, 285.623 );
		finishOri[14] = new Ypr( -1.546, 0.000, 0.000 );
		finishPos[15] = new Vector3( -878.330, 12.977, 711.550 );
		finishOri[15] = new Ypr( 2.483, 0.000, 0.000 );
		finishPos[16] = new Vector3( -288.414, 11.409, -215.491 );
		finishOri[16] = new Ypr( 1.692, 0.000, 0.000 );
		finishPos[17] = new Vector3( 165.907, 17.337, -515.365 );
		finishOri[17] = new Ypr( 0.479, 0.000, 0.000 );

//		finishPos[18] = new Vector3( 732.562, -26.356, -198.408 );
//		finishOri[18] = new Ypr( -1.117, 0.000, 0.000 );
//		finishPos[19] = new Vector3( 510.675, -26.708, 59.257 );
//		finishOri[19] = new Ypr( 1.818, 0.000, 0.000 );
	}
}
