package com.example.costbox.chartview;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
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
	final String[] quickChoice = new String[]{"Today","Past 3 Days","Past Week","Past Month"};
	final String[] monthChoice = new String[]{"Janary", "February", "March", "April", 
			"May", "June", "July", "August", "September", "October", "November", "December"};
	private int choice = 0;
	private int day_diff = 0;
	private String start_date = new String();
	private String end_date = new String();
	private String today_date = new String();
	private String line_start = new String();
	private String line_end = new String();
	private int today_year, today_monthOfYear, today_dayOfMonth;
	private int start_year, start_monthOfYear, start_dayOfMonth;
	private int end_year, end_monthOfYear, end_dayOfMonth;
	
    //initialization of pie and dou	
	String[] categories;
	double[] values;
	final int[] COLORS = new int[] {0xFFECD078,0xFFD95B43,0xFFC02942,0xFF542437,0xFF53777A};
	

	//initialization of line
	String[] titles = new String[] {"Daily Cost"};
	List<double[]> xvalue = new ArrayList<double[]>();
	List<double[]> yvalue = new ArrayList<double[]>();
	
	public void getPieValue(String startdate,  String enddate){
		int start = Integer.parseInt(startdate);
		int end = Integer.parseInt(enddate);
		Map<String, Double> result = new HashMap<String, Double>(200);
		result = CostBox.myCostDB.sum_Of_catogory_Of_days(start, end);
		
		categories = new String[result.size()];
		values = new double[result.size()];
		
		Iterator iter = result.entrySet().iterator();
		int temp = 0;
		while(iter.hasNext())
		{
			Map.Entry entry =(Map.Entry)iter.next();
			categories[temp] = (String) entry.getKey();
			values[temp] = (Double) Double.parseDouble(entry.getValue().toString());
			temp++;
		}
	}
	
	public void getLineValue(String startdate,  String enddate){
		int start = Integer.parseInt(startdate);
		int end = Integer.parseInt(enddate);
		Map<Integer, Double> result = new HashMap<Integer, Double>(200);
		result = CostBox.myCostDB.sum_up_Of_days(start, end);
		
		Object[] key =  result.keySet().toArray();    
		Arrays.sort(key);
		
		double x_tmp[] = new double[result.size()];
		double y_tmp[] = new double[result.size()];

		int temp = 0;
		for(int i = 0; i<key.length; i++){
			x_tmp[temp] = (Double) Double.parseDouble(key[i].toString()) % 100;
			y_tmp[temp] = (Double) Double.parseDouble(result.get(key[i]).toString());
			temp++;
		}
		for(int i=0;i<x_tmp.length;i++)
			Log.v("aaa", "aaa "+ x_tmp[i] + " aaa " + y_tmp[i]);
		
		xvalue.clear();
		yvalue.clear();
		xvalue.add(x_tmp);
		yvalue.add(y_tmp);
	}
	PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND};
	int[] colors = new int[] { Color.BLUE };
	
	// others
	private TextView startDate, endDate;
	
	private Button startchoose, endchoose, piechart, doughnutchart, linechart, quickchoice, monthchoice;
	private GraphicalView myChartView;
	
	
	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary);
		
		intent=this.getIntent();
		
		// 将日期选择的 end 定位到今天，start 根据 Preference 来做
		startDate = (TextView) findViewById(R.id.startdate);
		endDate = (TextView) findViewById(R.id.enddate);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
		String source = settings.getString("summary_view", "");  
		if(source.equals("004")) day_diff = 0;
		else if(source.equals("005")) day_diff = -2;
		else if(source.equals("006")) day_diff = -6;
		else if(source.equals("007")) day_diff = -29;
		
		
		Calendar calendar = Calendar.getInstance();		
		today_year = calendar.get(Calendar.YEAR);
		today_monthOfYear = calendar.get(Calendar.MONTH);
		today_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int today_month = today_monthOfYear + 1;
	    String today_formattedMonth = "" + today_month;
	    String today_formattedDayOfMonth = "" + today_dayOfMonth;
	    if(today_month < 10) today_formattedMonth = "0" + today_month;
	    if(today_dayOfMonth < 10) today_formattedDayOfMonth = "0" + today_dayOfMonth;
		today_date = "" + today_year + today_formattedMonth + today_formattedDayOfMonth;

		start_date = com.example.costbox.CostBox.getDateStr(today_date, day_diff);
		start_year = Integer.parseInt(start_date.substring(0,4));
		start_monthOfYear = Integer.parseInt(start_date.substring(4,6))-1;
		start_dayOfMonth = Integer.parseInt(start_date.substring(6));
		startDate.setText(start_date.substring(0,4) + "-" +start_date.substring(4,6) + "-" + start_date.substring(6));
		
		end_date = com.example.costbox.CostBox.getDateStr(today_date, 0);
		end_year = Integer.parseInt(end_date.substring(0,4));
		end_monthOfYear = Integer.parseInt(end_date.substring(4,6))-1;
		end_dayOfMonth = Integer.parseInt(end_date.substring(6));
		endDate.setText(end_date.substring(0,4) + "-" +end_date.substring(4,6) + "-" + end_date.substring(6));
		
		getPieValue(start_date,end_date);
		showPieChart(categories, values);
		
		// Click the start date button, and choose the date, then update the pie chart
		startchoose = (Button) findViewById(R.id.start);
		startchoose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(Summary.this, DatePickerDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						start_year = year; start_monthOfYear = monthOfYear; start_dayOfMonth = dayOfMonth;
						int start_month = monthOfYear + 1;
					    String start_formattedMonth = "" + start_month;
					    String start_formattedDayOfMonth = "" + dayOfMonth;
					    if(start_month < 10) start_formattedMonth = "0" + start_month;
					    if(dayOfMonth < 10) start_formattedDayOfMonth = "0" + dayOfMonth;
					    
					    startDate.setText( year + "-" + start_formattedMonth + "-" + start_formattedDayOfMonth);
					    start_date = "" + year + start_formattedMonth + start_formattedDayOfMonth;
						
						getPieValue(start_date, end_date);
						showPieChart(categories, values);
						
					}
				} , start_year, start_monthOfYear, start_dayOfMonth);
				
				datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_NEGATIVE){
							
						}
					}
				});
				
				datePickerDialog.show();
			}
			
		});
		
		// Click the end date button, and choose the date, then update the pie chart
		endchoose = (Button) findViewById(R.id.end);
		endchoose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(Summary.this, DatePickerDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						end_year = year; end_monthOfYear = monthOfYear; end_dayOfMonth = dayOfMonth;
						int end_month = monthOfYear + 1;
					    String end_formattedMonth = "" + end_month;
					    String end_formattedDayOfMonth = "" + dayOfMonth;
					    if(end_month < 10) end_formattedMonth = "0" + end_month;
					    if(dayOfMonth < 10) end_formattedDayOfMonth = "0" + dayOfMonth;
					    
						endDate.setText( year + "-" + end_formattedMonth + "-" + end_formattedDayOfMonth);
						end_date = "" + year + end_formattedMonth + end_formattedDayOfMonth;
						
						getPieValue(start_date, end_date);
						showPieChart(categories, values);
						
					}
				}, end_year, end_monthOfYear, end_dayOfMonth);
				
				datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_NEGATIVE){
							
						}
					}
				});
				
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
		
		// Click the quick choice button, and get the corresponding start date and end date, then update the chart
		quickchoice = (Button) findViewById(R.id.quickchoice);
		quickchoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder quick_choice = new AlertDialog.Builder(Summary.this, AlertDialog.THEME_HOLO_LIGHT);
				quick_choice.setTitle("Quick Choice for Pie Chart");
				quick_choice.setSingleChoiceItems(quickChoice, choice, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						choice = which;
					}
				});
				quick_choice.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.v("hhh","hhh123" + quickChoice[choice]);
							// 这里需要添加和之前类似的代码，重新 getPieValue
							if(choice == 0) day_diff = 0;
							else if(choice == 1) day_diff = -2;
							else if(choice == 2) day_diff = -6;
							else if(choice == 3) day_diff = -29;
							start_date = com.example.costbox.CostBox.getDateStr(today_date, day_diff);
							startDate.setText(start_date.substring(0,4) + "-" +start_date.substring(4,6) + "-" + start_date.substring(6));
							end_date = com.example.costbox.CostBox.getDateStr(today_date, 0);
							endDate.setText(end_date.substring(0,4) + "-" +end_date.substring(4,6) + "-" + end_date.substring(6));
							getPieValue(start_date, end_date);
							showPieChart(categories, values);
						}
					});
				quick_choice.setNegativeButton("Cancel", null);
				quick_choice.show();
			}
		});
		
		// Click the line button, choose the month, and display the line chart of daily cost of corresponding month
		linechart = (Button) findViewById(R.id.linechart);
		linechart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder month_choice = new AlertDialog.Builder(Summary.this, AlertDialog.THEME_HOLO_LIGHT);
				month_choice.setTitle("Choose Month");
				month_choice.setSingleChoiceItems(monthChoice, choice, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						choice = which;						
					}
				});
				month_choice.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int month = choice + 1;
					    String formattedMonth = "" + month;
					    if(month < 10) formattedMonth = "0" + month;
					    line_start = "2014" + formattedMonth + "01";
						line_end = "2014" + formattedMonth + "31";
						
						getLineValue(line_start, line_end);
						showLineChart(titles,xvalue, yvalue);
					}
				});
				month_choice.setNegativeButton("Cancel", null);
				month_choice.show();
			}
		});
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
				
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
		
		int[] colorTmp = new int[categoryString.length];
		for(int i=0;i<categoryString.length;i++){
			colorTmp[i] = COLORS[i%5];
		}
		final DefaultRenderer myRenderer = buildCategoryRenderer(colorTmp);
		
		myRenderer.setChartTitle("Category Sum of Cost from " + start_date.substring(0,4) + "-" 
				+ start_date.substring(4,6) + "-" + start_date.substring(6) + " to " 
				+ end_date.substring(0,4) + "-" + end_date.substring(4,6) + "-" + end_date.substring(6));
		myRenderer.setChartTitleTextSize(50);
		myRenderer.setLabelsTextSize(30);
		myRenderer.setLegendTextSize(30);
		myRenderer.setLabelsColor(Color.BLACK);
		myRenderer.setZoomEnabled(false);
		myRenderer.setShowLabels(true);
		myRenderer.setDisplayValues(true);
		myRenderer.setPanEnabled(false);
		myRenderer.setShowLegend(true);
		
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
					Toast.makeText(Summary.this, "The cost of " 
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
		
		int[] colorTmp = new int[category.length];
		for(int i=0;i<category.length;i++){
			colorTmp[i] = COLORS[i%5];
		}
		final DefaultRenderer myRenderer = buildCategoryRenderer(colorTmp);
		
		myRenderer.setChartTitle("Category Sum of Cost from " + start_date.substring(0,4) + "-" 
				+ start_date.substring(4,6) + "-" + start_date.substring(6) + " to " 
				+ end_date.substring(0,4) + "-" + end_date.substring(4,6) + "-" + end_date.substring(6));
		myRenderer.setChartTitleTextSize(50);
		myRenderer.setLabelsTextSize(30);
		myRenderer.setLegendTextSize(30);
		myRenderer.setLabelsColor(Color.BLACK);
		myRenderer.setZoomEnabled(false);
		myRenderer.setShowLabels(true);
		myRenderer.setDisplayValues(true);
		myRenderer.setPanEnabled(false);
		myRenderer.setShowLegend(false);
		
		myChartView = ChartFactory.getDoughnutChartView(this, mySeries, myRenderer);
		myChartView.repaint();
		layout.addView(myChartView);
	}
	
	
	public void showLineChart(final String[] titles, final List<double[]> xvalue, final List<double[]> yvalue){
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		layout.removeAllViews();
		
		
		XYMultipleSeriesDataset myDataset = buildDataset(titles, xvalue, yvalue);
		XYMultipleSeriesRenderer myRenderer = buildRenderer(colors, styles);

//		double[] x_tmp = xvalue.get(0);
//		double x_min=999999999, x_max = -999999999;
//		for(int i=0;i<x_tmp.length;i++){
//			if(x_tmp[i]<x_min) x_min=x_tmp[i];
//			if(x_tmp[i]>x_max) x_max=x_tmp[i];
//		}
		
		double[] y_tmp = yvalue.get(0);
		double y_min=999999999, y_max = -999999999;
		for(int i=0;i<y_tmp.length;i++){
			if(y_tmp[i]<y_min) y_min=y_tmp[i];
			if(y_tmp[i]>y_max) y_max=y_tmp[i];
		}
		
		myRenderer.setChartTitle("Sum of daily cost in " + monthChoice[choice]);
		myRenderer.setChartTitleTextSize(50);
		myRenderer.setLabelsTextSize(30);
		myRenderer.setLegendTextSize(30);
		myRenderer.setPointSize(5);
		myRenderer.setShowLegend(false);

		for (int i = 0; i < 31; i++) {
			myRenderer.addXTextLabel(i+1, ""+(i+1));
		}
		
		myRenderer.setXLabels(0);
		myRenderer.setXAxisMin(0);
		myRenderer.setXAxisMax(32);

		myRenderer.setYAxisMin(y_min*0.8);
		myRenderer.setYAxisMax(y_max*1.2);
		
		myRenderer.setShowGrid(true);
		myRenderer.setZoomEnabled(false);
		myRenderer.setXLabelsAlign(Align.RIGHT);
		myRenderer.setYLabelsAlign(Align.RIGHT);
		myRenderer.setZoomButtonsVisible(false);
		myRenderer.setPanEnabled(false);
		
		myRenderer.setAxesColor(Color.BLACK);
		myRenderer.setLabelsColor(Color.BLACK);
		myRenderer.setBackgroundColor(Color.WHITE);
		myRenderer.setApplyBackgroundColor(true);
		myRenderer.setMarginsColor(Color.WHITE);
		
		myRenderer.setMargins(new int[] {30,200,30,30});
		
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
					Toast.makeText(Summary.this, "The total cost of day " + mySeriesSelection.getXValue()
							+ " in " +  monthChoice[choice] + " is " + mySeriesSelection.getValue() + ".",
							Toast.LENGTH_SHORT).show();;
				}
			}
		});
	}
	
//	public void showBarChart(final String[] titles, final List<double[]> xvalue, final List<double[]> yvalue){
//		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
//		layout.removeAllViews();
//		
//		
//		XYMultipleSeriesDataset myDataset = buildDataset(titles, xvalue, yvalue);
//		XYMultipleSeriesRenderer myRenderer = buildRenderer(colors, styles);
//		
//		myRenderer.setChartTitle("Line Chart Summary");
//		myRenderer.setXTitle("Month");
//		myRenderer.setAxisTitleTextSize(30);
//		myRenderer.setYTitle("Value");
//		
//		myRenderer.setXAxisMin(0.5);
//		myRenderer.setXAxisMax(12.5);
//		
//		myRenderer.setXLabels(12);
//		myRenderer.setYLabels(10);
//		myRenderer.setXLabelsAlign(Align.LEFT);
//		myRenderer.setYLabelsAlign(Align.LEFT);
//		myRenderer.setPanEnabled(true, false);
//		myRenderer.setZoomEnabled(false);
//		myRenderer.setBarSpacing(0.5f);
//		myRenderer.setZoomButtonsVisible(false);
//		
//		myRenderer.setAxesColor(Color.BLACK);
//		myRenderer.setLabelsColor(Color.BLACK);
//		myRenderer.setChartTitleTextSize(30);
//		myRenderer.setLabelsTextSize(30);
//		myRenderer.setLegendTextSize(20);
//		
//		myRenderer.setBackgroundColor(Color.WHITE);
//		myRenderer.setApplyBackgroundColor(true);
//		myRenderer.setMarginsColor(Color.WHITE);
//		
//		myRenderer.getSeriesRendererAt(0).setDisplayChartValues(true);
//		myRenderer.getSeriesRendererAt(1).setDisplayChartValues(true);
//		
//		myChartView = ChartFactory.getBarChartView(this, myDataset, myRenderer, Type.DEFAULT);
//		myChartView.repaint();
//		layout.addView(myChartView);
//		
//		
//	}
	
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
			r.setFillPoints(true);
			r.setDisplayChartValues(true);
			r.setDisplayChartValuesDistance(40);
			r.setChartValuesTextSize(30);
			renderer.addSeriesRenderer(r);
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  
	    if (keyCode == KeyEvent.KEYCODE_BACK
	              && event.getRepeatCount() == 0) {
                //do nothing...
	                 
	          return true;
	       } 
	      return super.onKeyDown(keyCode, event);
	  }

}