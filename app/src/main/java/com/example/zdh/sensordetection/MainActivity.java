package com.example.zdh.sensordetection;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;                                    //定义传感器管理类
    private Sensor magneticSensor;                                          //定义用来存放磁力值的传感器类型变量
    private Sensor accelerometerSensor;                                     //定义用来存放加速度值的传感器类型变量
    private Sensor gyroscopeSensor;                                         //定义用来存放陀螺仪值的传感器类型变量
    private TextView acca,accx,accy,accz;                                   //定义用来显示加速度计各轴数值的控件
    private TextView gyrox,gyroy,gyroz;                                     //定义用来显示陀螺仪各轴数值的控件
    private TextView anglecosxml,anglexml;                                  //定义用来显示角度余弦值和角度的控件
    private TextView positionxml,watchxml,curpositionxml,orientionxml;  //定义用来显示方向的控件
    private TextView levelanglexml;                                           //定义用来显示角度的控件

    private static final float NS2S = 1.0f / 1000000000.0f;                 // 将纳秒转化为秒
    private float timestamp;                                                 //存储时间
    private float angle[] = new float[3];                                   //存储角度数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //定义陀螺仪各轴数值所对应的输出控件
        acca = (TextView) findViewById(R.id.acca);
        accx = (TextView) findViewById(R.id.accx);
        accy = (TextView) findViewById(R.id.accy);
        accz = (TextView) findViewById(R.id.accz);
        //定义加速度计各轴数值所对应的输出控件
        gyrox = (TextView) findViewById(R.id.gyrox);
        gyroy = (TextView) findViewById(R.id.gyroy);
        gyroz = (TextView) findViewById(R.id.gyroz);
        //将控件变量与XML布局文件中的控件相匹配
        anglecosxml = (TextView) findViewById(R.id.anglecosxml);
        anglexml = (TextView) findViewById(R.id.anglexml);
        positionxml = (TextView) findViewById(R.id.positionxml);
        watchxml = (TextView) findViewById(R.id.watchxml);
        curpositionxml = (TextView) findViewById(R.id.curpositionxml);
        orientionxml = (TextView) findViewById(R.id.orientionxml);
        levelanglexml = (TextView) findViewById(R.id.levelanglexml);

        //通过getSystemService获得SensorManager实例对象
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //通过SensorManager实例对象获得想要的传感器对象:参数决定获取哪个传感器
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //注册传感器事件监听函数
        sensorManager.registerListener(this,gyroscopeSensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,magneticSensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    //下面的程序为传感器数据处理部分
    @Override
    public void onSensorChanged(SensorEvent event) {

        //以下程序为对陀螺仪原始数据的处理和输出
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // x,y,z分别存储坐标轴x,y,z上的加速度
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // 根据三个方向上的加速度值得到总的加速度值a
            float a = (float) Math.sqrt(x * x + y * y + z * z);         //求合加速度
            float angcos = (float) z/a;                                 //求手机与地面夹角的余弦值
            float angle = (float) (Math.acos(angcos) * 180 / Math.PI);  //利用反三角函数得到角度值

            //各数据保留两位小数
            BigDecimal tempa = new BigDecimal(a);
            a = (float)tempa.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            BigDecimal tempx = new BigDecimal(x);
            x = (float)tempx.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            BigDecimal tempy = new BigDecimal(y);
            y = (float)tempy.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            BigDecimal tempz = new BigDecimal(z);
            z = (float)tempz.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            BigDecimal tempanglecos = new BigDecimal(angcos);
            angcos = (float)tempanglecos.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            BigDecimal tempangle = new BigDecimal(angle);
            angle = (float)tempangle.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            //将浮点型数据转化为字符型数据，以便使用TextView控件输出
            CharSequence showacca=String.valueOf(+a);
            CharSequence showaccx=String.valueOf(+x);
            CharSequence showaccy=String.valueOf(+y);
            CharSequence showaccz=String.valueOf(+z);
            CharSequence showangcos=String.valueOf(angcos);
            CharSequence showangle=String.valueOf(angle);

            //在控件上显示加速度计各轴数值、角度余弦值、角度
            acca.setText(showacca);
            accx.setText(showaccx);
            accy.setText(showaccy);
            accz.setText(showaccz);
            anglecosxml.setText(showangcos);
            anglexml.setText(showangle);
            levelanglexml.setText(showangle);

            //下面为输出控制代码
            //当观看手机时候检测到手机与水平面的夹角小于某一个软件设定值，就报警提醒用户使用姿势不正确
            if(angle>30) {
                positionxml.setText("Allow");
                positionxml.setTextColor(Color.GREEN);
            }
            else {
                positionxml.setTextColor(Color.RED);
                positionxml.setText("Forbid");
            }

            //检测用户是否在观看手机
            if(angle>30) {
                watchxml.setText("Yes");
                watchxml.setTextColor(Color.GREEN);
            }
            else {
                watchxml.setTextColor(Color.RED);
                watchxml.setText("No");
            }

            //检测手机是正面向上还是正面向下
            if(z>0) {
                curpositionxml.setTextColor(Color.GREEN);
                curpositionxml.setText("UP");
            }
            else {
                curpositionxml.setTextColor(Color.RED);
                curpositionxml.setText("DOWN");
            }

            //检测用户所持手机的方向
            if(x>7){
                orientionxml.setText("LEFT");
            }
            else if(x<-7){
                orientionxml.setText("RIGHT");
            }
            else if(y>7){
                orientionxml.setText("FRONT");
            }
            else if(y<-7){
                orientionxml.setText("BACK");
            }
            else if(z>7) {
                orientionxml.setText("UP");
            }
            else if(z<-7){
                orientionxml.setText("DOWN");
            }
        }

        //以下程序为对陀螺仪原始数据的处理和输出
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            if(timestamp != 0){
                // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                final float dT = (event.timestamp -timestamp) * NS2S;

                // 将手机在各个轴上的旋转角度相加，得到当前位置相对于初始位置的旋转弧度
                angle[0] += event.values[0] * dT;
                angle[1] += event.values[1] * dT;
                angle[2] += event.values[2] * dT;

                // 将各轴弧度转化为角度
                float anglex = (float) Math.toDegrees(angle[0]);
                float angley = (float) Math.toDegrees(angle[1]);
                float anglez = (float) Math.toDegrees(angle[2]);

                //各轴数据保留两位小数
                BigDecimal tempanglex = new BigDecimal(anglex);
                anglex = (float)tempanglex.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                BigDecimal tempangley = new BigDecimal(angley);
                angley = (float)tempangley.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                BigDecimal tempanglez = new BigDecimal(anglez);
                anglez = (float)tempanglez.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                //将浮点型数据转化为字符型数据，以便使用TextView控件输出
                CharSequence showgyrox=String.valueOf(+anglex);
                CharSequence showgyroy=String.valueOf(+angley);
                CharSequence showgyroz=String.valueOf(+anglez);

                //在控件上显示陀螺仪各轴数值，即各轴积分后得到的角度
                gyrox.setText(showgyrox);
                gyroy.setText(showgyroy);
                gyroz.setText(showgyroz);
            }
            //将当前时间赋值给timestamp
            timestamp = event.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO Auto-generated method stub
    }

    @Override
    protected void onPause() {
//TODO Auto-generated method stub
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}