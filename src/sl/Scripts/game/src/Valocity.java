package java.game;

import java.io.*;
import java.util.*;
import java.util.resource.*;
import java.render.*;    //Text
import java.render.osd.*;
import java.render.osd.dialog.*;
import java.sound.*;


public class Valocity extends City {
    //keep track of it so it can be renewed when entering a higher club
    public Valocity() {
        posGarage[0] = new Vector3( -278.518, 9.8, 1033.002 );
        oriGarage[0] = new Ypr( 1.580, 0.000, 0.000 );

        posGarage[1] = new Vector3( 355.381, 1.600, 418.244 );
        oriGarage[1] = new Ypr( -1.763, 0.000, 0.000 );

        posGarage[2] = new Vector3( -531.138, 5.050, -149.357 );
        oriGarage[2] = new Ypr( 2.077, 0.000, 0.000 );

        map = new GroundRef( maps.city:0x00000001r );
        nav = new Navigator( -23.482, -24.45, 5.828, maps.city.smallmap:0x00000001r, maps.city.smallmap:0x00000002r, maps.city.smallmap:0x00000005r, 8, 8, 8 );

        //for nr testing only:
        //posGarage[0] = new Vector3( -144.518, 10.214, 404.002 );


    }

    public void enter( GameState prev_state )
    {
        if( prev_state instanceof RaceSetup)
        {
            if(( GameLogic.gameMode == GameLogic.GM_QUICKRACE )
                    || ( GameLogic.gameMode == GameLogic.GM_DEMO ))
            {
                if (!raceState)	//abandoned
                {
                    GameLogic.changeActiveSection( parentState );
                    return;
                }
            }
            //unpause world
        }
        else
        {
            Frontend.loadingScreen.show();
            GfxEngine.flush();

            if( prev_state instanceof Garage || prev_state instanceof MainMenu )
            {
                posStart = posGarage[GameLogic.player.club];
                oriStart = oriGarage[GameLogic.player.club];
            }

            if(( GameLogic.gameMode == GameLogic.GM_QUICKRACE )
                    || ( GameLogic.gameMode == GameLogic.GM_DEMO ))
            {	//segitjuk az engine-t, ne kelljen a garazs kornyeket betolteni eloszor
//				posStart = map.getNearestCross( posStart );
                posStart = map.getNearestCross( new Vector3(0.0, 0.0, 500.0), Math.random()*1000 );
            }

            //mar itt fel kell ra tenni, a regisztralodo kocsik miatt
            addNotification( map, EVENT_COMMAND, EVENT_SAME, null );

            float hour = GameLogic.getTime() / 3600;
            if( hour > 4 && hour < 22 )
            {	//day time settings
                map.addTraffic( new GameRef(cars.traffic.Taxi:0x00000006r), 80, 2, 5, 2);
                map.addTraffic( new GameRef(cars.traffic.Ambulance:0x00000006r), 20, 2, 5, 2);
                map.addTraffic( new GameRef(cars.traffic.FireEngine:0x00000006r), 12, 2, 5, 2);
                map.addTraffic( new GameRef(cars.traffic.Coach:0x00000006r), 30, 2, 10,2);
                map.addTraffic( new GameRef(cars.traffic.Schoolbus:0x00000006r), 55, 2, 10, 2);

                map.addTraffic( new GameRef(cars.traffic.ArmoredVan:0x00000006r), 20, 2, 5, 2);
                map.addTraffic( new GameRef(cars.traffic.Wagon:0x00000006r), 200, 2, 5, 2);
                map.addTraffic( new GameRef(cars.traffic.Erbilac:0x00000006r), 200, 2, 5, 2);
                map.addTraffic( new GameRef(cars.traffic.CivilVan:0x00000006r), 200, 2, 5, 2);

                //insert police & some randomly chosen opponents into traffic
                if( GameLogic.gameMode == GameLogic.GM_CARREER || GameLogic.gameMode == GameLogic.GM_FREERIDE  )
                {
                    map.addTraffic( GRT_POLICECAR, 30, 2, 5, 2);

                    for( int i=0; i<GameLogic.speedymen.length; i++ )
                    {
                        if( Math.random() > 0.2 )
                        {
                            Racer opp = GameLogic.speedymen[i];
                            if( opp instanceof Bot )
                            {
                                Bot botopp = opp;
                                if( !botopp.dummycar )
                                {
                                    botopp.dummycar = new GameRef();
                                    botopp.dummycar.create_native( map, new GameRef(botopp.botVd.id),	"0,-10000,0,0,0,0", "dummycar" );
                                    botopp.dummycar.command( "texture " + GameLogic.CARCOLORS[botopp.botVd.colorIndex] + " 1" );
                                    botopp.traffic_id = map.addTrafficCar(botopp.dummycar, null);
                                    map.notifyTrafficCar(botopp.traffic_id, 1);
                                }
                            }
                        }
                    }
                } else
                if( GameLogic.gameMode == GameLogic.GM_DEMO )
                {
                    map.addTraffic( GRT_POLICECAR, 50, 2, 5, 2);
                }

                map.setPedestrianDensity( 0.003 );
            }
            else
            {	//night time settings

                map.addTraffic( new GameRef(cars.traffic.Erbilac:0x00000006r), 20, 2, 5, 2);
                map.addTraffic( new GameRef(cars.traffic.CivilVan:0x00000006r), 10, 2, 5, 2);
                map.addTraffic( new GameRef(cars.traffic.Taxi:0x00000006r), 150, 2, 5, 2);
                if( GameLogic.gameMode == GameLogic.GM_CARREER || GameLogic.gameMode == GameLogic.GM_FREERIDE  )
                {
                    map.addTraffic( GRT_POLICECAR, 3, 2, 5, 2);
                    prepareNightRace();
                } else
                if( GameLogic.gameMode == GameLogic.GM_DEMO )
                {
                    map.addTraffic( GRT_POLICECAR, 20, 2, 5, 2);
                }
                map.setPedestrianDensity( 0.0005 );
            }

            map.addPedestrianType( new GameRef(humans:0x0057r) );
            map.addPedestrianType( new GameRef(humans:0x0058r) );
            map.addPedestrianType( new GameRef(humans:0x0059r) );
            map.addPedestrianType( new GameRef(humans:0x005Ar) );
            map.addPedestrianType( new GameRef(humans:0x005Br) );
            map.addPedestrianType( new GameRef(humans:0x0022r) );

            map.setWater(new Vector3(0.0,-8.0,-1500.0), new Vector3(0.0,1.0,0.0), 300.0, 50.0);
            map.addWaterLimit(new Vector3(0.0,0.0,-500.0), new Vector3(0.0,0.0,1.0));

            if( GameLogic.gameMode == GameLogic.GM_CARREER || GameLogic.gameMode == GameLogic.GM_SINGLECAR )
            {
                addTrigger( posGarage[0], null, Marker.RR_GARAGE1, "event_handlerGarage1", 6.0, "garage1_trigger" );
                addTrigger( posGarage[1], null, Marker.RR_GARAGE2, "event_handlerGarage2", 6.0, "garage2_trigger" );
                addTrigger( posGarage[2], null, Marker.RR_GARAGE3, "event_handlerGarage3", 6.0, "garage3_trigger" );
            }
        }

        super.enter( prev_state );

        //quickrace handling:
        if( !(prev_state instanceof RaceSetup) )
        {
            if( GameLogic.gameMode == GameLogic.GM_QUICKRACE )
            {
                challenger = player;
                challenged = raceBot;

                raceState = 1;

                //racesetup start
                if( !GameLogic.racesetup )
                    GameLogic.racesetup = new RaceSetup();
                GameLogic.changeActiveSection( GameLogic.racesetup );
            } else
            if( GameLogic.gameMode == GameLogic.GM_DEMO )
            {//quick start
                osdEnabled = 0;
                enableOsd(osdEnabled);

                challenger = demoBot;
                challenged = raceBot;

                raceState = 1;

                Vector3	pFinish;
                do{
                    pFinish = map.getNearestCross( posStart, 500+Math.random()*1000 );
                }while( !map.getStartDirection( posStart, pFinish ) );

                changeCamChase();
                startRace( posStart, pFinish, 0 );
            }
        }

    }

    public void exit( GameState next_state )
    {
        //citybe kene!
        if( !(next_state instanceof RaceSetup) )
        {
            removeAllTimers();

            for( int i=0; i<GameLogic.speedymen.length; i++ )
            {
                Racer opp = GameLogic.speedymen[i];
                if( opp instanceof Bot )
                {
                    Bot botopp = opp;
                    if( botopp.dummycar )
                    {
                        map.remTrafficCar(botopp.traffic_id);
                        botopp.traffic_id = 0;
                        botopp.dummycar.destroy();
                        botopp.dummycar.release();
                        botopp.dummycar = null;
                    }
                }
            }
        }

        super.exit( next_state );
    }

    public void event_handlerGarage1( GameRef obj_ref, int event, String param )
    {
        handleGarageTrigger( 1, event, param );
    }

    public void event_handlerGarage2( GameRef obj_ref, int event, String param )
    {
        handleGarageTrigger( 2, event, param );
    }

    public void event_handlerGarage3( GameRef obj_ref, int event, String param )
    {
        handleGarageTrigger( 3, event, param );
    }

    public void handleGarageTrigger( int clubGarage, int event, String param )
    {
        if( param.token(0).intValue() == player.car.id() )
        {
            if( event == EVENT_TRIGGER_ON )
            {
                if( !activeTrigger )
                    activeTrigger=clubGarage;
            }
            else
                activeTrigger=0;
        }
    }


    public void handleEvent( GameRef obj_ref, int event, int param )
    {
        super.handleEvent( obj_ref, event, param );

        if( event == EVENT_TIME )
        {
            if( param == 2 )        //one sec tick
            {
                if( activeTrigger > 0 )
                {
                    //csak akkor mehetunk haza, ha nem uldoz a rendor, vagy nem versenyzunk
                    if( !policeState && !raceState )
                    {
                        if( player.car.getSpeedSquare() < 0.25 )        //slow speed?
                        {
                            if( activeTrigger-1 <= player.club )
                            {
                                activeTrigger = 0;
                                if( 0 == new YesNoDialog( player.controller, Dialog.DF_MODAL|Dialog.DF_DEFAULTBG|Dialog.DF_FREEZE, "QUESTION", "Go back to the garage?" ).display() )
                                    GameLogic.changeActiveSection( GameLogic.garage );
                            }
                            else
                            {	//meg nem lehet bemenned
                                activeTrigger = 0;
                                new WarningDialog( player.controller, Dialog.DF_MODAL|Dialog.DF_DEFAULTBG|Dialog.DF_FREEZE, "INFO", "This is a higher club's garage, you can't enter!" ).display();
                            }
                        }
                    }
                }
            }
        }
    }
}
