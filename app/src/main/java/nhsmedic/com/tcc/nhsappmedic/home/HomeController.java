package nhsmedic.com.tcc.nhsappmedic.home;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.SubscriptionEventListener;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.MainActivity;
import nhsmedic.com.tcc.nhsappmedic.R;
import nhsmedic.com.tcc.nhsappmedic.home.adapter.HomeAdapter;
import nhsmedic.com.tcc.nhsappmedic.home.model.DiagnosisModel;
import nhsmedic.com.tcc.nhsappmedic.institution.adapter.InstitutionAdapter;
import nhsmedic.com.tcc.nhsappmedic.institution.util.HealthInstitutionSharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeController extends Fragment {


    @BindView(R.id.patientsRecyclerView)
    RecyclerView patientsRecyclerView;

    @BindView(R.id.homeLinearLayout)
    LinearLayout homeLinearLayout;

    @BindView(R.id.loadingLinearLayout)
    LinearLayout loadingLinearLayout;

    @BindView(R.id.emptyLinearLayout)
    LinearLayout emptyLinearLayout;

    public HomeController() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_controller, container, false);
        ButterKnife.bind(this, view);

        if (idHealthInstitution == null) {
            HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(getContext());
            idHealthInstitution = healthInstitutionSharedPreferences.getHealthInstitutionId();
            nameHealthInstitution = healthInstitutionSharedPreferences.getHealthInstitutionName();
            photoHealthInstitution = healthInstitutionSharedPreferences.getHealthInstitutionPhoto();

        }


        return view;
    }

    private Pusher pusher;

    private void setupPusher() {

        if (isAdded()) {
            PusherOptions options = new PusherOptions();
            options.setCluster("us2");
            pusher = new Pusher("6ba2a6129f4cf6d110a6", options);
            HealthInstitutionSharedPreferences healthInstitutionSharedPreferences = new HealthInstitutionSharedPreferences(getContext());

            com.pusher.client.channel.Channel channel = pusher.subscribe(healthInstitutionSharedPreferences.getHealthInstitutionId() + ";nurse");
            channel.bind("new-patient-in-list", new SubscriptionEventListener() {
                @Override
                public void onEvent(String channelName, String eventName, final String data) {
                  /*  if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rabbitMQHandler.close();
                                homeAdapter.clear();
                                rabbitMQHandler.subscribe();
                            }
                        });

                    }
*/
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            rabbitMQHandler.close();
                            if(homeAdapter != null) {
                                homeAdapter.clear();
                            }
                            rabbitMQHandler.subscribe();
                        }
                    });
                }
            });
        }


    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), result);
                roundedBitmapDrawable.setCircular(true);
                bmImage.setImageDrawable(roundedBitmapDrawable);
                // bmImage.setImageBitmap(result);

            } catch (Exception e) {

            }
        }
    }


    private RabbitMQHandler rabbitMQHandler = new RabbitMQHandler();

    public class RabbitMQHandler {

        private ConnectionFactory factory;
        private Connection connection;
        private Channel channel;

        public RabbitMQHandler() {
            factory = new ConnectionFactory();

            String uri = "amqp://aovdehgg:qTbF0k3KF2O2GRn9WSOwcwlvaevLhkFJ@buffalo.rmq.cloudamqp.com/aovdehgg";
            try {
                factory.setAutomaticRecoveryEnabled(false);
                factory.setUri(uri);
            } catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        }

        private Thread subscribeThread;

        public void subscribe() {
            if (isAdded()) {


                loadingLinearLayout.setVisibility(View.GONE);
                homeLinearLayout.setVisibility(View.GONE);
                emptyLinearLayout.setVisibility(View.VISIBLE);

                subscribeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final HealthInstitutionSharedPreferences sharedPreferences = new HealthInstitutionSharedPreferences(getContext());

                        try {
                            connection = factory.newConnection();
                            channel = connection.createChannel();
                            channel.basicQos(1);

                            String queue = sharedPreferences.getHealthInstitutionId(); // queue name
                            boolean durable = true; // durable - RabbitMQ will never lose the
                            // queue if a crash occurs
                            boolean exclusive = false; // exclusive - if queue only will be used
                            // by one connection
                            boolean autoDelete = false; // autodelete - queue is deleted when
                            // last consumer unsubscribes

                            channel.queueDeclare(queue, durable, exclusive, autoDelete, null);


                            while (true) {
                                final Consumer consumer = new DefaultConsumer(channel) {
                                    @Override
                                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                                               byte[] body) throws IOException {
                                        String message = new String(body, "UTF-8");
                                        System.out.println(" [x] Received '" + message + "'");
                                        try {
                                            JSONObject jsonObject = new JSONObject(message);

                                            final DiagnosisModel diagnosisModel = new DiagnosisModel();
                                            diagnosisModel.setIdDiagnosis(jsonObject.getString("idDiagnosis"));
                                            diagnosisModel.setDateDiagnosis(jsonObject.getString("dateDiagnosis"));
                                            diagnosisModel.setAnnotation(jsonObject.getString("annotation"));
                                            diagnosisModel.setIdPatient(jsonObject.getString("idPatient"));
                                            diagnosisModel.setIdPhysician(jsonObject.getString("idPhysician"));
                                            diagnosisModel.setPatientName(jsonObject.getString("patientName"));
                                            diagnosisModel.setPatientPhoto(jsonObject.getString("patientPhoto"));
                                            diagnosisModel.setDeliveryTag(envelope.getDeliveryTag());
                                            diagnosisModel.setChannel(channel);

                                            Handler mHandler = new Handler(Looper.getMainLooper());
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (isAdapterEmpty()) {

                                                        HomeAdapter homeAdapter = new HomeAdapter(getContext());
                                                        setHomeAdapter(homeAdapter);

                                                    }

                                                    if(homeAdapter != null) {
                                                        homeAdapter.add(diagnosisModel);

                                                    }
                                                    loadingLinearLayout.setVisibility(View.GONE);
                                                    homeLinearLayout.setVisibility(View.VISIBLE);
                                                    emptyLinearLayout.setVisibility(View.GONE);

                                                }
                                            });

/*                                            if(getActivity() != null) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                    }
                                                });


                                            }*/
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }
                                };

                                channel.basicConsume(queue, false, consumer);

                            }

                        } catch (Exception e1) {


                        }
                    }
                });

                subscribeThread.start();
            }

        }


        public void close() {
            try {
                channel.close();
                connection.close();
                subscribeThread.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (AlreadyClosedException e) {

            } catch (Exception e) {

            }
        }

    }


    private FragmentActivity activity = getActivity();


    @Override
    public void onPause() {
        super.onPause();
        rabbitMQHandler.close();
        if (pusher != null) {
            pusher.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("eNTROU NO ONRESUMO");
                rabbitMQHandler.subscribe();
                setupPusher();

                if (pusher != null) {
                    pusher.connect();
                }

            }
        }, 3000);


    }


    private String idHealthInstitution;
    private String nameHealthInstitution;
    private String photoHealthInstitution;

    private HomeAdapter homeAdapter;

    private void setHealthInstitution(String idHealthInstitution, String nameHealthInstitution, String photoHealthInstitution) {
        this.idHealthInstitution = idHealthInstitution;
        this.nameHealthInstitution = nameHealthInstitution;
        this.photoHealthInstitution = photoHealthInstitution;
    }

    public void setHomeAdapter(HomeAdapter homeAdapter) {
        this.homeAdapter = homeAdapter;
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        patientsRecyclerView.setLayoutManager(layout);
        patientsRecyclerView.setAdapter(homeAdapter);

    }

    public boolean isAdapterEmpty() {
        return homeAdapter == null;
    }


}
