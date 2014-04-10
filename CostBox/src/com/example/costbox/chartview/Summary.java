package com.example.costbox.chartview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.costbox.CostBox;
import com.example.costbox.R;

public class Summary extends Activity 
{
    //initialization of pie and sweet	
	String[] categories;// = new String[]{};// {"Food", "Haircut", "MTR", "Book", "Others"};
	double[] values;// = new double[]{}; //{ 120, 140, 110, 200, 190 };
	
	public void testgiug(){

		Map<String,Integer> result = new HashMap<String,Integer>(200);
		result = CostBox.myCostDB.sum_of_catogory();  
		
		categories = new String[result.size()];
		values = new double[result.size()];
		  
		Iterator iter = result.entrySet().iterator();
		int temp = 0;
		
		while(iter.hasNext())
		{
			Map.Entry entry =(Map.Entry)iter.next();
			//temps = temps + "key: "+ entry.getKey() +"\n";
			categories[temp] = (String) entry.getKey();
			// Log.v("hhhh","hhhh" + categories[temp].toString());
			//temps = temps + "value:" + entry.getValue() + "\n" ;
			values[temp] = (Double) Double.parseDouble(entry.getValue().toString());
			// Log.v("hhhh","hhhh" + values[temp]);
			temp++;
		}
	}
	  
	final int[] COLORS = new int[] {0xFFECD078,0xFFD95B43,0xFFC02942,0xFF542437,0xFF53777A};
	
	
	//initialization of line and bar
	String[] titles = new String[] {"Food", "Book"};
	List<double[]> xvalue = new ArrayList<double[]>();
	List<double[]> yvalue = new ArrayList<double[]>();
	public void addvalue(){
		for (int i = 0; i < titles.length; i++) {
			xvalue.add(new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
		}
		yvalue.add(new double[] {80,89,88,75,78,79,90,98,79,78,100,97});
		yvalue.add(new double[] {70,99,98,95,58,89,100,98,56,67,79,99});
	}

	PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND };
	int[] colors = new int[] { Color.BLUE, Color.MAGENTA };
	
	// others
	private TextView startDate, endDate;
	private int year, monthOfYear, dayOfMonth;
	
	private Button startchoose, endchoose, piechart, doughnutchart, linechart, barchart;
	private GraphicalView myChartView;
	
	
	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary);
		testgiug();
		addvalue();
		intent=this.getIntent();
		showPieChart(categories, values);
		
		startDate = (TextView) findViewById(R.id.startdate);
		endDate = (TextView) findViewById(R.id.enddate);
		
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		monthOfYear = calendar.get(Calendar.MONTH);
		dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		
		startchoose = (Button) findViewById(R.id.start);
		startchoose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(Summary.this, DatePickerDialog.THEME_HOLO_DARK,new DatePickerDialog.OnDateSetListener(){
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth){
						startDate.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
					}
				}, year, monthOfYear, dayOfMonth);

				datePickerDialog.show();
			}
			
		});
		endchoose = (Button) findViewById(R.id.end);
		endchoose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(Summary.this, DatePickerDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener(){
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth){
						endDate.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
					}
				}, year, monthOfYear, dayOfMonth);

				datePickerDialog.show();
			}
		});
		
		
		piechart = (Button) findViewById(R.id.piechart);
		piechart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPieChart(categories, values);
			}
		});
		
		doughnutchart = (Button) findViewById(R.id.doughnut);
		doughnutchart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDoughnutChart(categories, values);
				
			}
		});
		linechart = (Button) findViewById(R.id.linechart);
		linechart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLineChart(titles, xvalue, yvalue);
				
			}
		});
		barchart = (Button) findViewById(R.id.barchart);
		barchart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showBarChart(titles, xvalue, yvalue);
				
			}
		});
		
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		
		Log.e("FHT", "onConfigurationChanged:");
		
		if(this.getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_LANDSCAPE){
			//Toast.makeText(getApplicationContext(), "LANDSCAPE", Toast.LENGTH_SHORT).show();
			
			
		} else if(this.getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_PORTRAIT){
			//Toast.makeText(getApplicationContext(), "PORTRAIT", Toast.LENGTH_SHORT).show();
			
			this.setResult(RESULT_OK,intent);
			this.finish();
				
		}
		
	}
	
	public void showPieChart(String[] categoryString, double[] valueDouble){
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		layout.removeAllViews();
		
		
		final CategorySeries mySeries = buildCategoryDataset("", categoryString, valueDouble);
		final DefaultRenderer myRenderer = buildCategoryRenderer(COLORS);
		
		myRenderer.setLabelsTextSize(10);
		myRenderer.setLegendTextSize(50);
		myRenderer.setLabelsColor(Color.BLACK);
		myRenderer.setZoomEnabled(false);
		myRenderer.setShowLabels(true);
		myRenderer.setDisplayValues(true);
		myRenderer.setFitLegend(true);
		
		myChartView = ChartFactory.getPieChartView(this, mySeries, myRenderer);
		myChartView.repaint();
		layout.addView(myChartView);
		
		myRenderer.setClickEnabled(true);
		myChartView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SeriesSelection mySeriesSelection = myChartView.getCurrentSeriesAndPoint();
				if (mySeriesSelection == null) {
					Toast.makeText(Summary.this, "No item is selected", Toast.LENGTH_SHORT).show();
				} else {
					for (int i=0;i<mySeries.getItemCount();i++){
						myRenderer.getSeriesRendererAt(i).setHighlighted(i == mySeriesSelection.getPointIndex());
					}
					myChartView.repaint();
					Toast.makeText(Summary.this, "The expense of " 
							+ mySeries.getCategory(mySeriesSelection.getPointIndex()) 
							+ " is " + mySeriesSelection.getValue() + ".", Toast.LENGTH_SHORT).show();;
				}
			}
		});
	}
	
	public void showDoughnutChart(String[] category, double[] value){
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		layout.removeAllViews();
		
		List<String[]> categories = new ArrayList<String[]>();
		List<double[]> values = new ArrayList<double[]>();
		categories.add(category);
		values.add(value);
		
		final MultipleCategorySeries mySeries = buildMultipleCategoryDataset("", categories, values);
		final DefaultRenderer myRenderer = buildCategoryRenderer(COLORS);
		
		myRenderer.setLabelsTextSize(10);
		myRenderer.setLegendTextSize(50);
		myRenderer.setLabelsColor(Color.BLACK);
		myRenderer.setZoomEnabled(false);
		myRenderer.setShowLabels(true);
		myRenderer.setDisplayValues(true);
		myRenderer.setFitLegend(true);
		
		myChartView = ChartFactory.getDoughnutChartView(this, mySeries, myRenderer);
		myChartView.repaint();
		layout.addView(myChartView);
		
		myRenderer.setClickEnabled(true);
		myChartView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SeriesSelection mySeriesSelection = myChartView.getCurrentSeriesAndPoint();
				if (mySeriesSelection == null) {
					Toast.makeText(Summary.this, "No item is selected", Toast.LENGTH_SHORT).show();
				} else {
					for (int i=0;i<mySeries.getItemCount(0);i++){
						myRenderer.getSeriesRendererAt(i).setHighlighted(i == mySeriesSelection.getPointIndex());
					}
					myChartView.repaint();
					Toast.makeText(Summary.this, "The expense of " 
							+ mySeries.getCategory(mySeriesSelection.getPointIndex()) 
							+ " is " + mySeriesSelection.getValue(), Toast.LENGTH_SHORT).show();;
				}
			}
		});
	}
	
	/*
	 * The following methods are used to build the chart more quickly and conveniently.
	 */
	
	/*
	 * The following three methods are used for pie chart.
	 */
	protected CategorySeries buildCategoryDataset(String title, String[] categories, double[] values) {
		CategorySeries series = new CategorySeries(title);
		int length = categories.length;
		for (int i = 0; i < length; i++){
			series.add(categories[i], values[i]);
		}
		return series;
	}
	
	protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
			List<String[]> categories, List<double[]> values) {
		MultipleCategorySeries series = new MultipleCategorySeries(title);
		int k=0;
		for (double[] value : values) {
			series.add(categories.get(k), value);
			k++;
		}
		return series;
	}
	
	protected DefaultRenderer buildCategoryRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	
	public void showLineChart(final String[] titles, final List<double[]> xvalue, final List<double[]> yvalue){
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		layout.removeAllViews();
		
		
		XYMultipleSeriesDataset myDataset = buildDataset(titles, xvalue, yvalue);
		XYMultipleSeriesRenderer myRenderer = buildRenderer(colors, styles);
		
		myRenderer.setChartTitle("Line Chart Summary");
		myRenderer.setXTitle("Month");
		myRenderer.setAxisTitleTextSize(30);
		myRenderer.setYTitle("Value");
		myRenderer.setXLabels(12);
		myRenderer.setXAxisMin(0);
		myRenderer.setXAxisMax(12);
		myRenderer.setYAxisMin(0);
		myRenderer.setYAxisMax(100);
		myRenderer.setShowGrid(true);
		myRenderer.setZoomEnabled(false);
		myRenderer.setXLabelsAlign(Align.RIGHT);
		myRenderer.setYLabelsAlign(Align.RIGHT);
		myRenderer.setZoomButtonsVisible(false);
		
		myRenderer.setAxesColor(Color.BLACK);
		myRenderer.setLabelsColor(Color.BLACK);
		myRenderer.setChartTitleTextSize(30);
		myRenderer.setLabelsTextSize(30);
		myRenderer.setLegendTextSize(20);
		
		myRenderer.setBackgroundColor(Color.WHITE);
		myRenderer.setApplyBackgroundColor(true);
		myRenderer.setMarginsColor(Color.WHITE);
		
		myRenderer.setMargins(new int[] {20, 30, 15,0});
		
		myChartView = ChartFactory.getLineChartView(this, myDataset, myRenderer);
		myChartView.repaint();
		layout.addView(myChartView);
		
		myRenderer.setClickEnabled(true);
		myRenderer.setSelectableBuffer(100);
		myChartView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SeriesSelection mySeriesSelection = myChartView.getCurrentSeriesAndPoint();
				if (mySeriesSelection == null) {
					Toast.makeText(Summary.this, "No chart element", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(Summary.this, "The expense of " + titles[mySeriesSelection.getSeriesIndex()] 
							+ " at month " + mySeriesSelection.getXValue()
							+ " is " + mySeriesSelection.getValue() + ".", Toast.LENGTH_SHORT).show();;
				}
			}
		});
	}
	
	public void showBarChart(final String[] titles, final List<double[]> xvalue, final List<double[]> yvalue){
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		layout.removeAllViews();
		
		
		XYMultipleSeriesDataset myDataset = buildDataset(titles, xvalue, yvalue);
		XYMultipleSeriesRenderer myRenderer = buildRenderer(colors, styles);
		
		myRenderer.setChartTitle("Line Chart Summary");
		myRenderer.setXTitle("Month");
		myRenderer.setAxisTitleTextSize(30);
		myRenderer.setYTitle("Value");
		
		myRenderer.setXAxisMin(0.5);
		myRenderer.setXAxisMax(12.5);
		
		myRenderer.setXLabels(12);
		myRenderer.setYLabels(10);
		myRenderer.setXLabelsAlign(Align.LEFT);
		myRenderer.setYLabelsAlign(Align.LEFT);
		myRenderer.setPanEnabled(true, false);
		myRenderer.setZoomEnabled(false);
		myRenderer.setBarSpacing(0.5f);
		myRenderer.setZoomButtonsVisible(false);
		
		myRenderer.setAxesColor(Color.BLACK);
		myRenderer.setLabelsColor(Color.BLACK);
		myRenderer.setChartTitleTextSize(30);
		myRenderer.setLabelsTextSize(30);
		myRenderer.setLegendTextSize(20);
		
		myRenderer.setBackgroundColor(Color.WHITE);
		myRenderer.setApplyBackgroundColor(true);
		myRenderer.setMarginsColor(Color.WHITE);
		
		myRenderer.getSeriesRendererAt(0).setDisplayChartValues(true);
		myRenderer.getSeriesRendererAt(1).setDisplayChartValues(true);
		
		myChartView = ChartFactory.getBarChartView(this, myDataset, myRenderer, Type.DEFAULT);
		myChartView.repaint();
		layout.addView(myChartView);
		
		
	}
	
	
	/*
	 * The following methods are used for line charts
	 */
	
	protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}
	
	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,List<double[]> yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}
	
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}
	
	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
	}
	
	private void SumOfCategory()
	  {
		/*
		  Map<String,Integer> result = new HashMap<String,Integer>(200);
		  
		  
		  Integer t1,t2; 
		  t1 = Integer.valueOf(1);
		  t2 = Integer.valueOf(2);
		  result.put("first", t1);
		  result.put("second",t2);
		  
		  */
		  Map<String,Integer> result = new HashMap<String,Integer>(200);
		  result = CostBox.myCostDB.sum_of_catogory();
		  
		  Iterator iter = result.entrySet().iterator();
		  String temps ="";
		  while(iter.hasNext())
		  {
			  Map.Entry entry =(Map.Entry)iter.next();
			  temps = temps + "key: "+ entry.getKey() +"\n";
			  temps = temps + "value:" + entry.getValue() + "\n" ; 
		  }
		  
		  Toast.makeText(this, temps, Toast.LENGTH_SHORT).show();
	  }
}