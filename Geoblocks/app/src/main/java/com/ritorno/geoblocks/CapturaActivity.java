package com.ritorno.geoblocks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ritorno.geoblocks.avatar.AvatarActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;


public class CapturaActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private Sensor accelerometer;
    public ImageView img;
    public ImageView pokebola;
    public int dimenX;  //dimensao horizontal da tela em pixel
    public int dimenY;  //dimensao vertical da tela em pixel
    public float centerX;   //centro horizontal ajustado
    public float centerY;   //centro vertical ajustado
    public float escalaX;   //usada para converter leituras em pixel
    public float escalaY;   //usada para converter leituras em pixel
    public float centerXpokeball;   //centro horizontal ajustado
    public float centerYpokeball;   //centro vertical ajustado
    public float grauXtotal = 0;
    public float grauYtotal = 0;
    public float grauZtotal = 0;
    public float grauXnovo = 0;
    public float grauYnovo = 0;
    public float grauZnovo = 0;
    public float grauXant = 0;
    public float grauYant = 0;
    public float grauZant = 0;
    float distanciaTopoY;
    float distanciaBaseY;
    float distanciaEsquerdaX;
    float distanciaDireitaX;
    public float percentImagePokemon = (float) 0.5;
    public boolean imagemPokemonPreparada = false;
    public float larguraImgPokemon = 0;
    public float alturaImgPokemon = 0;
    public float[] limitesPokemon;
    public float percentImagePokeball = (float) 0.15;
    public boolean imagemPokeballPreparada = false;
    public float larguraImgPokeball = 0;
    public float alturaImgPokeball = 0;
    public float[] limitesPokeball;

    public boolean capturou = false;

    public float xInicioTouch = 0;
    public float yInicioTouch = 0;
    public float xFimTouch = 0;
    public float yFimTouch = 0;
    public long tempoInicial = 0;
    public long tempoFinal = 0;
    float diferencaX;
    float diferencaY;
    long duracaoTouch;
    float velocidadeX; //pixel por milisegundo
    float velocidadeY; //pixel por milisegundo
    float velocidadeXoriginal;
    float velocidadeYoriginal;

    public int countSound = 0;

    private CameraPreview mPreview;
    private Camera mCamera;

    private TextView nomePkmnCaptura;
    private int[] tabDrawables;
    private int[] coins;
    int id;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        id = intent.getIntExtra("pkmn", 0);
        type = intent.getStringExtra("type");
        //setContentView(R.layout.view_captura);
        preparaCamera();

        tabDrawables = new int[] {
                R.drawable.nft_1,R.drawable.nft_2,
                R.drawable.nft_3, R.drawable.nft_4,
                R.drawable.nft_5, R.drawable.nft_6,R.drawable.nft_7,R.drawable.nft_8,R.drawable.nft_9,R.drawable.nft_10,
                R.drawable.nft_11,R.drawable.nft_12, R.drawable.nft_13,R.drawable.nft_14,R.drawable.nft_15,R.drawable.nft_16,
                R.drawable.nft_17,R.drawable.nft_18 };

        coins = new int[] {
                R.drawable.coi_1,R.drawable.coi_2,
                R.drawable.coi_3, R.drawable.coi_4,
                R.drawable.coi_5, R.drawable.coi_6,R.drawable.coi_7,R.drawable.coi_8 };

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dimenX = size.x;
        dimenY = size.y;

    }

    @Override
    protected void onResume() {
        super.onResume();

        nomePkmnCaptura = (TextView) findViewById(R.id.txtNomePkmnCaptura);
        pokebola = (ImageView) findViewById(R.id.pokeball);
        img = (ImageView) findViewById(R.id.pokemon);


        if(Objects.equals(type, "Golden Box"))
        {
            nomePkmnCaptura.post(new Runnable() {
                @Override
                public void run() {
                    nomePkmnCaptura.setText("Congratulations you found a Coin !!");
                    nomePkmnCaptura.measure(0,0);
                    nomePkmnCaptura.setX(dimenX - nomePkmnCaptura.getMeasuredWidth() - ViewUnitsUtil.convertDpToPixel(8));
                }
            });
            img.setImageResource(coins[id]);
            pokebola.setImageResource(R.drawable.pokeball2);
        }else if(Objects.equals(type, "Silver Box")){
            nomePkmnCaptura.post(new Runnable() {
                @Override
                public void run() {
                    nomePkmnCaptura.setText("Congratulations, you found a NFT !!");
                    nomePkmnCaptura.measure(0,0);
                    nomePkmnCaptura.setX(dimenX - nomePkmnCaptura.getMeasuredWidth() - ViewUnitsUtil.convertDpToPixel(8));
                }
            });
            img.setImageResource(tabDrawables[id]);
            pokebola.setImageResource(R.drawable.pokeball1);
        }


        configuraPokebola();
        configuraPokemon();

        if(sensor != null){
            sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_GAME);
        }

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
        imagemPokemonPreparada = false;
        imagemPokeballPreparada = false;

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void animate(double fromDegrees, double toDegrees, long durationMillis, ImageView img) {
        final RotateAnimation rotate = new RotateAnimation((float) fromDegrees, (float) toDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        img.clearAnimation();
        rotate.setDuration(durationMillis);
        rotate.setFillEnabled(true);
        rotate.setFillAfter(true);
        img.startAnimation(rotate);
    }

    private HashMap<Integer, Long> timestamp = new HashMap<>();

    private double getSensorElapsedSeconds(SensorEvent event) {
        Long lastTimestamp = timestamp.put(event.sensor.getType(), event.timestamp);

        if (lastTimestamp == null)
            return 0;

        return (event.timestamp - lastTimestamp) / 1000000000f;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GYROSCOPE:
                onGyroscopeChanged(event);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                onAccelerationChanged(event);
                break;
            default:
                throw new IllegalStateException("Unreachable");
        }

    }

    double accelerationNoise, speed, distance;
    long accelerationSamples;

    private double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    private void onAccelerationChanged(SensorEvent event) {
        double elapsed = getSensorElapsedSeconds(event);

        double accelerationSensor = -event.values[2];
        double accelerationSensorMagnitude = Math.abs(accelerationSensor);
        double accelerationSensorDirection = Math.signum(accelerationSensor);

        accelerationNoise += (1 / ++accelerationSamples) * (accelerationSensorMagnitude - accelerationNoise);

        if (imagemPokemonPreparada && imagemPokeballPreparada) {
            // Atenua o ruído na aceleração.
            double acceleration = Math.max(accelerationSensorMagnitude - accelerationNoise, 0) * accelerationSensorDirection;

            speed = clamp(speed + acceleration * elapsed, -0.25f, +0.25f);
            distance = clamp(distance + speed * elapsed, 0.25f, 0.75f);

            percentImagePokemon = 1f - (float) distance;
            configuraPokemon();

            Log.i("Accel", String.format("A=%.1f S=%.1f D=%.1f N=%.1f", acceleration, speed, distance, accelerationNoise));
        }
    }

    public void onGyroscopeChanged(SensorEvent event) {
        if(imagemPokemonPreparada && imagemPokeballPreparada) {
            float xNovo = img.getX();
            float yNovo = img.getY();

            WindowManager.LayoutParams params = this.getWindow().getAttributes();
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            this.getWindow().setAttributes(params);

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            grauXnovo = (float) ((x * 57.2958) * 0.02);
            grauYnovo = (float) ((y * 57.2958) * 0.02);
            grauZnovo = (float) ((z * 57.2958) * 0.02);

            grauXtotal += grauXnovo;
            grauYtotal += grauYnovo;
            grauZtotal += grauZnovo;

            Log.i("Posição", "X: " + x + " Y: " + y + " Z: " + z);
            Log.i("GrauNovo", "X: " + grauXnovo + " Y: " + grauYnovo + " Z: " + grauZnovo);
            Log.i("Grau", "X: " + grauXtotal + " Y: " + grauYtotal + " Z: " + grauZtotal);

            if (grauYtotal > grauYant + 0.01 || grauYtotal < grauYant - 0.01) {
                xNovo = img.getX() + (grauYnovo * escalaX);
                img.setX(xNovo);
            } else {
                grauYtotal = grauYant;
            }

            if (grauXtotal > grauXant + 0.01 || grauXtotal < grauXant - 0.01) {
                yNovo = img.getY() + (grauXnovo * escalaY);
                img.setY(yNovo);
            } else {
                grauXtotal = grauXant;
            }

            if (grauZtotal > grauZant + 0.01 || grauZtotal < grauZant - 0.01) {

                Log.d("Pivot", "X: " + img.getPivotX() + " Y: " + img.getPivotY());

                img.setRotation(grauZtotal);
            } else {
                grauZtotal = grauZant;
            }

            grauXant = grauXtotal;
            grauYant = grauYtotal;
            grauZant = grauZtotal;

            Log.i("IMAGEM", "X: " + img.getX() + " Y: " + img.getY());


            if (grauYtotal < 0) {

                if (Math.abs(Math.abs(grauYtotal) - 360) <= distanciaDireitaX / escalaX) {
                    img.setX(dimenX - 10);
                    centerX = img.getX();
                    distanciaEsquerdaX = centerX;
                    distanciaDireitaX = dimenX - centerX;
                    grauYtotal = 0;
                }
            }

            if (grauYtotal > 0) {

                if (Math.abs(Math.abs(grauYtotal) - 360) <= (distanciaEsquerdaX + larguraImgPokemon) / escalaX) {

                    Log.d("Passei", "DE: " + distanciaEsquerdaX + " DimX: " + dimenX + " PI: " + percentImagePokemon + " EX: " + escalaX);
                    Log.d("Passei", "estive aqui horizontal " + grauYtotal + " " + (distanciaEsquerdaX + larguraImgPokemon) / escalaX);


                    img.setX(-larguraImgPokemon);
                    centerX = img.getX();

                    distanciaEsquerdaX = escalaX - larguraImgPokemon;
                    distanciaDireitaX = dimenX - centerX;
                    grauYtotal = 0;
                }
            }


            if (grauXtotal > 0) {

                if (Math.abs(Math.abs(grauXtotal) - 360) <= (distanciaTopoY + alturaImgPokemon) / escalaY) {
                    Log.d("Passei", "estive aqui vertical " + grauXtotal + " " + (distanciaTopoY + alturaImgPokemon) / escalaY);


                    img.setY(-alturaImgPokemon);
                    centerY = img.getY();

                    distanciaTopoY = escalaY - alturaImgPokemon;
                    distanciaBaseY = dimenY - centerY;
                    grauXtotal = 0;
                }
            }

            if (grauXtotal < 0) {

                if (Math.abs(Math.abs(grauXtotal) - 360) <= distanciaBaseY / escalaY) {
                    img.setY(dimenY - 10);
                    centerY = img.getY();
                    distanciaTopoY = centerY;
                    distanciaBaseY = dimenY - centerY;
                    grauXtotal = 0;
                }
            }

            limitesPokemon = getLeftRightTopBottomImage(img.getX(),img.getY(),alturaImgPokemon,larguraImgPokemon);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //mudou a precisão
    }

    public void preparaCamera(){
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_capture);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPreview = new CameraPreview(this);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        frameLayout.addView(mPreview);

        AbsoluteLayout absolutLayoutControls = (AbsoluteLayout) findViewById(R.id.imagemAR);
        absolutLayoutControls.bringToFront();

    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public float[] getLeftRightTopBottomImage(float x, float y, float altura, float largura){
        float limites[] = new float[4];
        limites[0] = x;
        limites[1] = x + largura;
        limites[2] = y;
        limites[3] = y + altura;

        Log.d("Limites", "E: " + limites[0] + " D: " + limites[1] + " C: " + limites[2] + " B: " + limites[3]);
        return limites;
    }

    public void configuraPokemon(){
        //garante só rodar após as views estarem na tela
        img.post(new Runnable() {
            @Override
            public void run() {
                //define largura do pokemon em relação ao tamanho da tela
                larguraImgPokemon = dimenX * percentImagePokemon;
                //obtem propoção da imagem redimensionada
                float proporcaoPokemon = (larguraImgPokemon * 100) / img.getMeasuredWidth();
                //define altura do pokemon de forma proporcional
                alturaImgPokemon = img.getMeasuredHeight() * proporcaoPokemon / 100;

                //obtem o centro da tela
                //COLOCAR ELE RANDOMICO Mais pra frente
                centerX = dimenX / 2 - (((int) larguraImgPokemon) / 2);
                centerY = dimenY / 2 - (((int) alturaImgPokemon) / 2);

                //X: 1200 Y: 1834 CX: 300.0 CY: 459.0 IMG_X: 600 IMG_Y: 917

                //modifica o tamanho da imagem e centraliza o mesmo na tela
                AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams((int) larguraImgPokemon, (int) alturaImgPokemon, (int) centerX, (int) centerY);
                img.setLayoutParams(params);

                //calcula distâncias iniciais
                distanciaTopoY = centerY;
                distanciaBaseY = dimenY - centerY;
                distanciaEsquerdaX = centerX;
                distanciaDireitaX = dimenX - centerX;

                //calcula a escala
                escalaX = dimenX / 72; //cada grau vale escala pixel - 72º é o campo de visão considerado
                escalaY = dimenY / 72;

                Log.i("Dimensão", "X: " + dimenX + " Y: " + dimenY + " CX: " + centerX + " CY: " + centerY +
                        " IMG_X: " + (int) larguraImgPokemon + " IMG_Y: " + (int) alturaImgPokemon);

                imagemPokemonPreparada = true;
            }
        });
    }

    public void configuraEfeitoCaptura(){

        pokebola.animate().rotation(0).start();
        img.setImageResource(R.drawable.explosion);
        Toast.makeText(this, "Mint Successfull !!", Toast.LENGTH_LONG).show();
        this.finish();
        img.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(350);
                    img.setVisibility(View.INVISIBLE);
                } catch (Exception e) {

                }
            }
        });
    }

    public void atribuirVolumeMediaPlayer(MediaPlayer mediaPlayer, int volume){
        int maxVolume = 100;
        float log1=(float)(Math.log(maxVolume-volume)/Math.log(maxVolume));
        mediaPlayer.setVolume(1-log1,1-log1);
    }

    public void configuraPokebola(){
        pokebola.post(new Runnable() {
            @Override
            public void run() {
                larguraImgPokeball = 400;
                float proporcaoPokebola = (larguraImgPokeball * 50) / pokebola.getMeasuredWidth();
                alturaImgPokeball = 400;

                centerXpokeball = dimenX / 2 - (((int) larguraImgPokeball) / 2);
                centerYpokeball = dimenY - (int) alturaImgPokeball - 75;
                AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams((int) larguraImgPokeball, (int) alturaImgPokeball, (int) centerXpokeball, (int) centerYpokeball);
                pokebola.setLayoutParams(params);

                Log.i("Dimensão", "X: " + dimenX + " Y: " + dimenY + " CX_pokeball: " + centerXpokeball + " CY_pokeball: " + centerYpokeball +
                        " IMG_X_pokeball: " + (int) larguraImgPokeball + " IMG_Y_pokeball: " + (int) alturaImgPokeball);

                imagemPokeballPreparada = true;
            }
        });

        configuraTouchPokebola();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void configuraTouchPokebola(){
        //configura o listener de toque na pokebola
        pokebola.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO: PESQUISAR O COMPORTAMENTO EXATO DOS MÉTODOS getRawX() e getRawY()
                float x = event.getRawX();
                float y = event.getRawY();
                Log.d("Mover Pokebola", "X: " + x + " Y: " + y);

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:

                        tempoInicial = System.currentTimeMillis();
                        xInicioTouch = pokebola.getX();
                        yInicioTouch = pokebola.getY();

                        Log.d("Mover Pokebola", "Tocou na pokebola");
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        //tratamento se retirou o dado da imagem

                        tempoFinal = System.currentTimeMillis();
                        xFimTouch = pokebola.getX();
                        yFimTouch = pokebola.getY();

                        diferencaX = Math.abs(xInicioTouch - xFimTouch);
                        diferencaY = Math.abs(yInicioTouch - yFimTouch);
                        duracaoTouch = tempoFinal - tempoInicial;

                        velocidadeX = diferencaX / duracaoTouch; //pixel por milisegundo
                        velocidadeY = diferencaY / duracaoTouch; //pixel por milisegundo

                        //inicia a Thread
                        //deslocamentoPokebola = new DeslocamentoPokebola();
                        //deslocamentoPokebola.execute(); //MÉTODO doInBackground da class DeslocamentoPokebola é executado

                        velocidadeXoriginal = velocidadeX;
                        velocidadeYoriginal = velocidadeY;

                        while (velocidadeX > 0 && velocidadeY > 0 && !capturou) {
                            int tempo = 25; //compensa a velocidade para a pokebola acelerar mais
                            //pokebola arremessada para a direita
                            if (xFimTouch >= xInicioTouch) {
                                pokebola.setX(pokebola.getX() + (tempo * velocidadeX));
                            } else {
                                pokebola.setX(pokebola.getX() - (tempo * velocidadeX));
                            }

                            //pokebola arremessada para cima
                            if (yFimTouch <= yInicioTouch) {
                                pokebola.setY(pokebola.getY() - (tempo * velocidadeY));
                            } else {
                                pokebola.setY(pokebola.getY() + (tempo * velocidadeY));
                            }

                            //---------------VERIFICA CAPTURA-------------
                            //obtem limites da pokebola
                            limitesPokeball = getLeftRightTopBottomImage(pokebola.getX(), pokebola.getY(), alturaImgPokeball, larguraImgPokeball);
                            Log.d("LimPokeball", "E: " + limitesPokeball[0] + " D: " + limitesPokeball[1] + " C: " + limitesPokeball[2] + " B: " + limitesPokeball[3]);

                            //verifica se houve captura pela interseção de imagens
                            if (isCapturou(limitesPokeball, limitesPokemon, capturou)) {
                                capturou = true;
                                configuraEfeitoCaptura();
                                Log.w("Mover Pokebola", "Capturou por arremesso " + getTime());
                                break;
                            }
                            //--------------------------------------------

                            Log.d("Deslocamento", "Atualizei..VX: " + velocidadeX + " VY: " + velocidadeY);

                            //reduzindo a velocidade da pokebola
                            velocidadeX = velocidadeX - (velocidadeXoriginal * (float) 0.045);
                            velocidadeY = velocidadeY - (velocidadeYoriginal * (float) 0.045);
                        }

                        if(pokebola.getX() > dimenX || pokebola.getX() < 0 ||
                                pokebola.getY() > dimenY || pokebola.getY() < 0){

                            Toast.makeText(getBaseContext(),"Tente novamente...",Toast.LENGTH_SHORT).show();

                            pokebola.setX(centerXpokeball);
                            pokebola.setY(centerYpokeball);
                        }

                        Log.d("Mover Pokebola", "Retirou o dedo da pokebola");
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //tratamento para arrastar imagem
                        Log.d("Mover Pokebola", "Moveu a pokebola");

                        //pokebola.setX(x - (larguraImgPokeball / 2));
                        //pokebola.setY(y - (alturaImgPokeball / 2));

                        pokebola.setX(x - (larguraImgPokeball / 2));
                        pokebola.setY((y - (alturaImgPokeball / 3)) - (alturaImgPokeball / 2)); //-(alturaImgPokeball / 3) para suavizar o movimento

                        //---------------VERIFICA CAPTURA-------------
                        //obtem limites da pokebola
                        limitesPokeball = getLeftRightTopBottomImage(pokebola.getX(), pokebola.getY(), alturaImgPokeball, larguraImgPokeball);
                        Log.d("LimPokeball", "E: " + limitesPokeball[0] + " D: " + limitesPokeball[1] + " C: " + limitesPokeball[2] + " B: " + limitesPokeball[3]);

                        //verifica se houve captura pela interseção de imagens
                        if (isCapturou(limitesPokeball, limitesPokemon, capturou)) {
                            Log.w("Mover Pokebola", "Capturou por tocar " + getTime());
                            capturou = true;
                            configuraEfeitoCaptura();
                        }
                        //---------------------------------------------

                        return true;

                    default:
                        Log.d("Mover Pokebola", "Evento não classificado na pokebola");
                }

                return false;
            }
        });
    }

    public String getTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    //verifica interseção
    public boolean isCapturou(float pkball[],float pkmn[], boolean cap){
        if(pkball[0] <= pkmn[1] &&
                pkmn[0] <= pkball[1] &&
                pkball[2] <= pkmn[3] &&
                pkmn[2] <= pkball[3] && !cap){
            return true;
        }else{
            return false;
        }
    }

   /*private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }*/

    class DeslocamentoPokebola extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(Colaboracao.this);
            pDialog.setMessage("ENVIANDO...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

        protected String doInBackground(String... args) {
            /*
                        tempoFinal = System.currentTimeMillis();
                        xFimTouch = pokebola.getX();
                        yFimTouch = pokebola.getY();

                        diferencaX = Math.abs(xInicioTouch - xFimTouch);
                        diferencaY = Math.abs(yInicioTouch - yFimTouch);
                        duracaoTouch = tempoFinal - tempoInicial;

                        velocidadeX = diferencaX/duracaoTouch; //pixel por milisegundo
                        velocidadeY = diferencaY/duracaoTouch; //pixel por milisegundo
             */

            velocidadeY = velocidadeY*3;
            velocidadeX = velocidadeX*3;

            velocidadeXoriginal = velocidadeX;
            velocidadeYoriginal = velocidadeY;

            while(velocidadeX > 0 && velocidadeY > 0){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int tempo = 100;
                        //pokebola arremessada para a direita
                        if(xFimTouch >= xInicioTouch) {
                            pokebola.setX(pokebola.getX() + (tempo * velocidadeX));
                        }else{
                            pokebola.setX(pokebola.getX() - (tempo * velocidadeX));
                        }

                        //pokebola arremessada para cima
                        if(yFimTouch <= yInicioTouch) {
                            pokebola.setY(pokebola.getY() - (tempo * velocidadeY));
                        }else{
                            pokebola.setY(pokebola.getY() + (tempo * velocidadeY));
                        }

                        try {
                            Thread.sleep(tempo);
                        }catch (Exception e){
                            Log.e("ERRO", "sleep asyncTask");
                        }

                        Log.d("Deslocamento", "Atualizei..VX: " + velocidadeX + " VY: " + velocidadeY);

                        velocidadeX = velocidadeX - (velocidadeXoriginal*(float)0.03);
                        velocidadeY = velocidadeY - (velocidadeYoriginal*(float)0.03);

                        if(velocidadeX < 0 || velocidadeY < 0)
                            return;

                    }
                });

            }



            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            /*pDialog.dismiss();
            Log.i("CANCELOU", "CANCELOU ASYNC TASK");*/
        }

        protected void onPostExecute(String file_url) {
            //pDialog.dismiss();
            Log.d("Deslocamento", "acabei..");
        }
    }

}


