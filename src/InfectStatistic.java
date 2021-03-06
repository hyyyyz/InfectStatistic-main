import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.*;
import java.util.Set;
import java.util.Map.Entry;

class InfectStatistic { 
	
	public  String logPath;  //日志文件所在地址
	public  String outPath;  //输出文件所在地址
	
	 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
	 Date d = new Date(System.currentTimeMillis());
	public  String date = formatter.format(d);//当前时间设置
	
	//设置-type下的信息
	public  int[] type = new int [5];
	public static  ArrayList<String> typelist = new ArrayList<>();//要求的类型 
	public  int  isTypeExist = 0; 
	public  String[] typeStr = {"感染患者", "疑似患者", "治愈", "死亡"};  
	
	//设置-province下的信息
	public  int[] province = new int [35];  
	public  int[] provinceExist = new int [35] ; 
	public  ArrayList<String> provincelist = new ArrayList<>();//要求的省份
	public  int  isProvinceExist = 0;   
	public static String[] provinceStr = {"全国", "安徽", "澳门" ,"北京", "重庆", "福建","甘肃","广东", "广西", "贵州", "海南", "河北", "河南", "黑龙江", "湖北", "湖南", 
			"吉林","江苏", "江西", "辽宁", "内蒙古", "宁夏", "青海", "山东", "山西", "陕西", "上海","四川", "台湾", "天津", "西藏", "香港", "新疆", "云南", "浙江"};
	
	public   int[][] peopleNumber = new int [35][4];  //记录全国以及每个省份每个类型的人数，初始默认为0，按照上面类型和省份顺序排列
	
	public static void main(String[] args) {  //主函数入口
		
		InfectStatistic infectStatistic = new InfectStatistic();
    	InfectStatistic.cmdArgs cmdArgs = infectStatistic.new cmdArgs(args);
    	if(!cmdArgs.checkCmd()) {
            return;
        }
    	
    	 	InfectStatistic.fileDispose filehandle = infectStatistic.new fileDispose();
        	filehandle.readFileList();
        	filehandle.writeOutTxt();
		
	}
		
	static class Province{
		private String name;//省份名称
		private int ip;//感染
		private int sp;//疑似
		private int cure;//治愈
		private int dead;//死亡
		
		Province(String s, int ip, int sp, int cure, int dead){
		this.name = s;
		this.ip = ip;
		this.sp = sp;
		this.cure = cure;
		this.dead = dead;
		}
		
		public String getName(){
		return name;
		}
		
		public int getIp(){
		return ip;
		}
		
		public int getSp(){
		return sp;
		}
		
		public int getCure(){
		return cure;
		}
		
		public int getDead() {
		return dead;
		}
		
		public void setIp(int ip) {
		this.ip = ip;
		}
		
		public void setSp(int sp) {
		this.sp = sp;
		}
		
		public void setCure(int cure) {
		this.cure = cure;
		}
		
		public void setDead(int dead) {
		this.dead = dead;
		}
		
		public String printResult(){
		StringBuilder result = new StringBuilder(name);
		if(typelist != null && !typelist.isEmpty()) {//type有参数
		for (String s : typelist) {
		if (s.equals("ip")) {
		result.append(" 感染患者").append(ip).append("人");
		}
		if (s.equals("sp")) {
		result.append(" 疑似患者").append(sp).append("人");
		}
		if (s.equals("cure")) {
		result.append(" 治愈").append(cure).append("人");
		}
		if (s.equals("dead")) {
		result.append(" 死亡").append(dead).append("人");
		}
		}
		}
		else {//type没有参数
		result.append(" 感染患者").append(ip).append("人").append(" 疑似患者").
		append(sp).append("人").append(" 治愈").append(cure).append("人").
		append(" 死亡").append(dead).append("人");
		}
		return result.toString();
		}

		 public int getPosition() {
            int position = 0;
            for(int i  = 0; i < provinceStr.length; i++) {
                if(provinceStr[i].equals(name)) {
                    position = i;
                    break;
                }
            }
            return position;
        }
	}
    class cmdArgs{
        String[] args;
       
        cmdArgs(String[] args) {
            this.args = args;
        }
       
        public boolean checkCmd() {
            if(!args[0].equals("list")) {
                System.out.println("error：命令不存在");
                return false;
            }
            if(!has()) {
                System.out.println("error：命令缺少必要的参数");
                return false;
            }
            for(int i = 1; i < args.length; i++) {
                switch (args[i]) {
                    case "-log":
                        i = getLogPath(i);
                        if (i == -1) {
                            System.out.println("error：日志路径有误");
                            return false;
                        }
                        break;
                    case "-out":
                        i = getOutPath(i);
                        if (i == -1) {
                            System.out.println("error：输出路径有误");
                            return false;
                        }
                        break;
                    case "-date":
                        i = getDate(i);
                        if (i == -1) {
                            System.out.println("error：日期参数值有误");
                            return false;
                        }
                        break;
                    case "-type":
                        i = getType(i);
                        if (i == -1) {
                            System.out.println("error：要求的格式参数值有误");
                            return false;
                        }
                        break;
                    case "-province":
                        i = getProvince(i);
                        if (i == -1) {
                            System.out.println("error：要求的省份参数值有误");
                            return false;
                        }
                        break;
                    default:
                        System.out.println("error：未知参数");
                        return false;
                }
            }
            return true;
        }

        boolean has() {
            return Arrays.asList(args).contains("-log") && Arrays.asList(args).contains("-out");
        }
        
        public int getLogPath(int i) {
            i++;
            if(i < args.length) {
                logPath = args[i];
            } else
                return -1;
            return i;
        }
       
        public int getOutPath(int i) {
            i++;
            if(i < args.length) {
                outPath = args[i];
            } else
                return -1;
            return i;
        }
       
        public int getDate(int i) {
            i++;
            if(i < args.length) {
                if(date.compareTo(args[i]) >= 0)
                    date = args[i];
                else
                    return -1;
            } else
                return -1;
            return i;
        }
        
        public int getType(int i) {
            i++;
            int j = i;
            if(i < args.length) {
                label:
                while(i<args.length) {
                    switch (args[i]) {
                        case "ip":
                        case "cure":
                        case "sp":
                        case "dead":
                            typelist.add(args[i]);
                            i++;
                            break;
                        default:
                            break label;
                    }
                }
            }
            if(j == i)
                return -1;
            return (i - 1);
        }

        public int getProvince(int i) {
            i++;
            int j = i;
            if(i < args.length) {
                while(i<args.length) {
                    if(Arrays.asList(provinceStr).contains(args[i])) {
                        provincelist.add(args[i]);
                        i++;
                    } else
                        break;
                }
            }
            if(j == i)
                return -1;
            return (i - 1);
        }
    }
	
	
	class fileDispose{  //文件处理类
		
		fileDispose(){};  //空构造函数
		
		public void readFileList() {  //读取指定路径下的文件名
			date = date + ".log.txt";  //把date加上后缀
			File file = new File(logPath);
			  String[] files = file.list();  //list()方法是返回某个目录下的所有文件和目录的文件名，返回的是String数组
			 for(int j = 0; j < files.length; j++){
				if(files[j].compareTo(date) <= 0) {  //判断该文件时间是否小于指定时间
					readLogTxt(logPath + files[j]);  //开始读取日志文件内容
				}
			}
		}
		
		public String readLogTxt(String filePath){ 
			try {
				BufferedReader bfr = new BufferedReader
			    (new InputStreamReader(new FileInputStream(new File(filePath)),"UTF-8")); //通过普通的缓存方式文本读取，编码为UTF-8
			    String readLine = null;
			    while ((readLine = bfr.readLine()) != null) {  
			    	if(! readLine.startsWith("//"))  //遇到“//”不读取
			    		init();//开始进行文本内容的处理
			        }
			    bfr.close();  
			 	}  	catch (Exception e) {  
			 			e.printStackTrace();
			    }
			return null;
			
		} 
		
		public void init() throws IOException {
			ArrayList<Province> result;//省份列表
			String content = readLogTxt(logPath);//读取文件夹下的文件
			result = match(content, provincelist);//正则表达式匹配
			HashMap<Integer, Province> resultmap;
			resultmap = sort(result);
			outResult(resultmap, provincelist, outPath);//输出结果
		}
		
		

		private HashMap<Integer, Province> sort(ArrayList<Province> result) {
            HashMap<Integer, Province> result_map = new HashMap<>();
            int country_ip, country_sp, country_cure, country_dead;
            country_ip = country_sp = country_cure = country_dead = 0;
            for (InfectStatistic.Province province : result) {
                country_ip += province.getIp();
                country_sp += province.getSp();
                country_cure += province.getCure();
                country_dead += province.getDead();
                result_map.put(( province).getPosition(), province);
            }
            Province country = new Province("全国", country_ip, country_sp, country_cure, country_dead);
            result_map.put(0, country);
            return result_map;
        }
		

		public ArrayList<Province> match(String content, ArrayList<String> province_list) {
		ArrayList<Province> result = new ArrayList<>();
		if(province_list != null && !province_list.isEmpty()) {
		for(String s : province_list) {
		Province p =new Province(s, 0, 0, 0, 0);
		result.add(p);
		}
		}
		String pattern1 = "(\\S+) 新增 感染患者 (\\d+)人";
		String pattern2 = "(\\S+) 新增 疑似患者 (\\d+)人";
		String pattern3 = "(\\S+) 感染患者 流入 (\\S+) (\\d+)人";
		String pattern4 = "(\\S+) 疑似患者 流入 (\\S+) (\\d+)人";
		String pattern5 = "(\\S+) 死亡 (\\d+)人";
		String pattern6 = "(\\S+) 治愈 (\\d+)人";
		String pattern7 = "(\\S+) 疑似患者 确诊感染 (\\d+)人";
		String pattern8 = "(\\S+) 排除 疑似患者 (\\d+)人";
		try {
		BufferedReader br = new BufferedReader(
		new InputStreamReader(new ByteArrayInputStream(content.getBytes())));
		String line = "";
		line = br.readLine();
		while ((line = br.readLine()) != null) {
		Matcher matcher1 = Pattern.compile(pattern1).matcher(line);
		Matcher matcher2 = Pattern.compile(pattern2).matcher(line);
		Matcher matcher3 = Pattern.compile(pattern3).matcher(line);
		Matcher matcher4 = Pattern.compile(pattern4).matcher(line);
		Matcher matcher5 = Pattern.compile(pattern5).matcher(line);
		Matcher matcher6 = Pattern.compile(pattern6).matcher(line);
		Matcher matcher7 = Pattern.compile(pattern7).matcher(line);
		Matcher matcher8 = Pattern.compile(pattern8).matcher(line);
		while (matcher1.find()) {
		addIP(result, matcher1);
		}
		while (matcher2.find()) {
		addSP(result, matcher2);
		}
		while (matcher3.find()) {
		moveIP(result, matcher3);
		}
		while (matcher4.find()) {
		moveSP(result, matcher4);
		}
		while (matcher5.find()) {
		addDead(result, matcher5);
		}
		while (matcher6.find()) {
		addCure(result, matcher6);
		}
		while (matcher7.find()) {
		diagnosisSp(result, matcher7);
		}
		while (matcher8.find()) {
		excludeSp(result, matcher8);
		}
		}
		
		} catch (Exception e) {
		e.printStackTrace();
		}
		return result;
		}
		
		
		public void addIP(ArrayList<Province> result, Matcher matcher) {
			boolean b = false;
			for (InfectStatistic.Province province : result) {
			if (province.getName().equals(matcher.group(1))) {
			b = true;
			province.setIp(Integer.parseInt(matcher.group(2)) + province.getIp());
			}
			}
			if(!b) {//省份不存在
			Province p =new Province(matcher.group(1), Integer.parseInt(matcher.group(2)), 0, 0, 0);
			result.add(p);
			}
			}
		
		public void addSP(ArrayList<Province> result, Matcher matcher) {
            boolean b = false;
            for (InfectStatistic.Province province : result) {
                if (province.getName().equals(matcher.group(1))) {
                    b = true;
                    province.setSp(Integer.parseInt(matcher.group(2)) + province.getSp());
                }
            }
            if(!b) {//省份不存在
                Province p =new Province(matcher.group(1), 0, Integer.parseInt(matcher.group(2)), 0, 0);
                result.add(p);
            }
        }
		
		private void moveIP(ArrayList<Province> result, Matcher matcher) {
            int out = -1;//流出省
            int in = -1;//流入省
            for(int i = 0; i < result.size(); i++){
                if(result.get(i).getName().equals(matcher.group(1))){
                    out = i;
                }
                if(result.get(i).getName().equals(matcher.group(2))){
                    in = i;
                }
            }
            if(out == -1) {//流出省份不存在
                System.out.println("流出省份" + matcher.group(1) + "不存在感染患者，数据有误");
            }
            else {
                //修改流出省的感染患者人数
                if(in == -1) {//流入省份不存在
                    Province p =new Province(matcher.group(2),
                            Integer.parseInt(matcher.group(3)), 0, 0, 0);
                    result.add(p);
                } else {
                    result.get(in).setIp(result.get(in).getIp() +
                            Integer.parseInt(matcher.group(3)));//修改流入省的感染患者人数
                }
                result.get(out).setIp(result.get(out).getIp() -
                        Integer.parseInt(matcher.group(3)));//修改流出省的感染患者人数
            }
        }

        private void moveSP(ArrayList<Province> result, Matcher matcher) {
            int out = -1;//流出省
            int in = -1;//流入省
            for(int i = 0; i < result.size(); i++){
                if(result.get(i).getName().equals(matcher.group(1))){
                    out = i;
                }
                if(result.get(i).getName().equals(matcher.group(2))){
                    in = i;
                }
            }
            if(out == -1) {//流出省份不存在
                System.out.println("流出省份" + matcher.group(1) + "不存在疑似患者，数据有误");
            } else {
                //修改流出省的感染患者人数
                if(in == -1) {//流入省份不存在
                    Province p =new Province(matcher.group(2), 0,
                            Integer.parseInt(matcher.group(3)), 0, 0);
                    result.add(p);
                } else {
                    result.get(in).setSp(result.get(in).getSp() +
                            Integer.parseInt(matcher.group(3)));//修改流入省的感染患者人数
                }
                result.get(out).setSp(result.get(out).getSp() -
                        Integer.parseInt(matcher.group(3)));//修改流出省的感染患者人数
            }
        }

        private void addDead(ArrayList<Province> result, Matcher matcher) {
            boolean b = false;
            for (InfectStatistic.Province province : result) {
                if (province.getName().equals(matcher.group(1))) {
                    b = true;
                    province.setIp(province.getIp() - Integer.parseInt(matcher.group(2)));//修改该省份的感染患者人数
                    province.setDead(Integer.parseInt(matcher.group(2)) + province.getDead());//修改该省份的死亡人数
                }
            }
            if(!b) {//省份不存在
                System.out.println("死亡省份" + matcher.group(1) + "不存在感染患者，数据有误");
            }
        }

        private void addCure(ArrayList<Province> result, Matcher matcher) {
            boolean b = false;
            for (InfectStatistic.Province province : result) {
                if (province.getName().equals(matcher.group(1))) {
                    b = true;
                    province.setIp(province.getIp() - Integer.parseInt(matcher.group(2)));//修改该省份的感染患者人数
                    province.setCure(Integer.parseInt(matcher.group(2)) + province.getCure());//修改该省份的治愈人数
                }
            }
            if(!b) {//省份不存在
                System.out.println("治愈省份" + matcher.group(1) + "不存在感染患者，数据有误");
            }
        }

        private void diagnosisSp(ArrayList<Province> result, Matcher matcher) {
            boolean b = false;
            for (InfectStatistic.Province province : result) {
                if (province.getName().equals(matcher.group(1))) {
                    b = true;
                    province.setIp(Integer.parseInt(matcher.group(2)) + province.getIp());//修改该省份的感染患者人数
                    province.setSp(province.getSp() - Integer.parseInt(matcher.group(2)));//修改该省份的疑似患者人数
                }
            }
            if(!b) {//省份不存在
                System.out.println("确诊疑似省份" + matcher.group(1) + "不存在疑似患者，数据有误");
            }
        }

        private void excludeSp(ArrayList<Province> result, Matcher matcher) {
            boolean b = false;
            for (InfectStatistic.Province province : result) {
                if (province.getName().equals(matcher.group(1))) {
                    b = true;
                    province.setSp(province.getSp() - Integer.parseInt(matcher.group(2)));//修改该省份的疑似患者人数
                }
            }
            if(!b) {//省份不存在
                System.out.println("确诊疑似省份" + matcher.group(1) + "不存在疑似患者，数据有误");
            }
        }
		
        private void outResult(HashMap<Integer, Province> result_map,
                ArrayList<String> provincelist, String outpath) throws IOException {
        		
        				initFile(outpath);
        				FileWriter fw = new FileWriter(outpath, true);
        				BufferedWriter bw = new BufferedWriter(fw);
        				Set<Entry<Integer, Province>> entries =result_map.entrySet();
        				if(provincelist != null && !provincelist.isEmpty()) {//province有参数值
        					for(Entry<Integer, Province> entry:entries){
        						if(provincelist.contains(provinceStr[entry.getKey()])) {
        							bw.write(entry.getValue().printResult());
        							try {
										bw.write("\n");
									} catch (IOException e) {
										// TODO 自动生成的 catch 块
										e.printStackTrace();
									}//换行
        						}
        					}
        				} else {
        					for(Entry<Integer, Province> entry:entries ){
        						try {
									bw.write(entry.getValue().printResult());
								} catch (IOException e1) {
									// TODO 自动生成的 catch 块
									e1.printStackTrace();
								}
        						try {
									bw.write("\n");
								} catch (IOException e) {
									// TODO 自动生成的 catch 块
									e.printStackTrace();
								}//换行
        					}
        				}
        				bw.write("// 该文档并非真实数据，仅供测试使用");
        				bw.close();
        				fw.close();
        		 
        }
        
		private void initFile(String filename) {
			
            try {
            	FileWriter fw = new FileWriter(filename);
				fw.write("");
				fw.flush();
	            fw.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}

			
		}

		public void writeOutTxt() {  //输出文件内容
			FileWriter fwriter = null;
			int i, j;	
			provinceExist[0] = 1; 
			try {
				fwriter = new FileWriter(outPath);  
				if(isProvinceExist == 0){  //若-province未指定
					for(i = 0; i < 35; i++){
						if(provinceExist[i] == 1){
							fwriter.write(provinceStr[i] + " ");
							if(isTypeExist == 0){  //若-type未指定
								for( j = 0; j < 4; j++)
									fwriter.write(typeStr[j] + peopleNumber[i][j] + "人 ");}
							
						
							else{  //若-type指定
								for( j = 0; j < 4; j++)
									if(type[j] != 0)
									fwriter.write(typeStr[type[j]-1] + peopleNumber[i][type[j]-1] + "人 ");
							}
							fwriter.write("\n");
						}
					}
				}
				else {  //若-province有指定
					for(i = 0; i < 35; i++){
						if(province[i] == 1){
							fwriter.write(provinceStr[i] + " ");
							if(isTypeExist == 0){  //若-type未指定
								for( j = 0; j < 4; j++)
									fwriter.write(typeStr[j] + peopleNumber[i][j] + "人 ");
							}
							else{  //若-type指定
								for( j = 0; j < 4; j++)
									if(type[j] != 0)
										fwriter.write(typeStr[type[j]-1] + peopleNumber[i][type[j]-1] + "人 ");
							} 
							fwriter.write("\n");
						}
							
					}
				}
				        
				fwriter.write("// 该文档并非真实数据，仅供测试使用");
				 }
				 catch (Exception e) {
				        e.printStackTrace();
				    				 } finally {
				     try {
				         fwriter.flush();
				         fwriter.close();
				         }	catch (IOException e1) {
				            		e1.printStackTrace();
				         	}
				    				 	}
			
		}
	}
 
}
