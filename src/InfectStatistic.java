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
	
	public  String logPath;  //��־�ļ����ڵ�ַ
	public  String outPath;  //����ļ����ڵ�ַ
	
	 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
	 Date d = new Date(System.currentTimeMillis());
	public  String date = formatter.format(d);//��ǰʱ������
	
	//����-type�µ���Ϣ
	public  int[] type = new int [5];
	public static  ArrayList<String> typelist = new ArrayList<>();//Ҫ������� 
	public  int  isTypeExist = 0; 
	public  String[] typeStr = {"��Ⱦ����", "���ƻ���", "����", "����"};  
	
	//����-province�µ���Ϣ
	public  int[] province = new int [35];  
	public  int[] provinceExist = new int [35] ; 
	public  ArrayList<String> provincelist = new ArrayList<>();//Ҫ���ʡ��
	public  int  isProvinceExist = 0;   
	public static String[] provinceStr = {"ȫ��", "����", "����" ,"����", "����", "����","����","�㶫", "����", "����", "����", "�ӱ�", "����", "������", "����", "����", 
			"����","����", "����", "����", "���ɹ�", "����", "�ຣ", "ɽ��", "ɽ��", "����", "�Ϻ�","�Ĵ�", "̨��", "���", "����", "���", "�½�", "����", "�㽭"};
	
	public   int[][] peopleNumber = new int [35][4];  //��¼ȫ���Լ�ÿ��ʡ��ÿ�����͵���������ʼĬ��Ϊ0�������������ͺ�ʡ��˳������
	
	public static void main(String[] args) {  //���������
		
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
		private String name;//ʡ������
		private int ip;//��Ⱦ
		private int sp;//����
		private int cure;//����
		private int dead;//����
		
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
		if(typelist != null && !typelist.isEmpty()) {//type�в���
		for (String s : typelist) {
		if (s.equals("ip")) {
		result.append(" ��Ⱦ����").append(ip).append("��");
		}
		if (s.equals("sp")) {
		result.append(" ���ƻ���").append(sp).append("��");
		}
		if (s.equals("cure")) {
		result.append(" ����").append(cure).append("��");
		}
		if (s.equals("dead")) {
		result.append(" ����").append(dead).append("��");
		}
		}
		}
		else {//typeû�в���
		result.append(" ��Ⱦ����").append(ip).append("��").append(" ���ƻ���").
		append(sp).append("��").append(" ����").append(cure).append("��").
		append(" ����").append(dead).append("��");
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
                System.out.println("error���������");
                return false;
            }
            if(!has()) {
                System.out.println("error������ȱ�ٱ�Ҫ�Ĳ���");
                return false;
            }
            for(int i = 1; i < args.length; i++) {
                switch (args[i]) {
                    case "-log":
                        i = getLogPath(i);
                        if (i == -1) {
                            System.out.println("error����־·������");
                            return false;
                        }
                        break;
                    case "-out":
                        i = getOutPath(i);
                        if (i == -1) {
                            System.out.println("error�����·������");
                            return false;
                        }
                        break;
                    case "-date":
                        i = getDate(i);
                        if (i == -1) {
                            System.out.println("error�����ڲ���ֵ����");
                            return false;
                        }
                        break;
                    case "-type":
                        i = getType(i);
                        if (i == -1) {
                            System.out.println("error��Ҫ��ĸ�ʽ����ֵ����");
                            return false;
                        }
                        break;
                    case "-province":
                        i = getProvince(i);
                        if (i == -1) {
                            System.out.println("error��Ҫ���ʡ�ݲ���ֵ����");
                            return false;
                        }
                        break;
                    default:
                        System.out.println("error��δ֪����");
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
	
	
	class fileDispose{  //�ļ�������
		
		fileDispose(){};  //�չ��캯��
		
		public void readFileList() {  //��ȡָ��·���µ��ļ���
			date = date + ".log.txt";  //��date���Ϻ�׺
			File file = new File(logPath);
			  String[] files = file.list();  //list()�����Ƿ���ĳ��Ŀ¼�µ������ļ���Ŀ¼���ļ��������ص���String����
			 for(int j = 0; j < files.length; j++){
				if(files[j].compareTo(date) <= 0) {  //�жϸ��ļ�ʱ���Ƿ�С��ָ��ʱ��
					readLogTxt(logPath + files[j]);  //��ʼ��ȡ��־�ļ�����
				}
			}
		}
		
		public String readLogTxt(String filePath){ 
			try {
				BufferedReader bfr = new BufferedReader
			    (new InputStreamReader(new FileInputStream(new File(filePath)),"UTF-8")); //ͨ����ͨ�Ļ��淽ʽ�ı���ȡ������ΪUTF-8
			    String readLine = null;
			    while ((readLine = bfr.readLine()) != null) {  
			    	if(! readLine.startsWith("//"))  //������//������ȡ
			    		init();//��ʼ�����ı����ݵĴ���
			        }
			    bfr.close();  
			 	}  	catch (Exception e) {  
			 			e.printStackTrace();
			    }
			return null;
			
		} 
		
		public void init() throws IOException {
			ArrayList<Province> result;//ʡ���б�
			String content = readLogTxt(logPath);//��ȡ�ļ����µ��ļ�
			result = match(content, provincelist);//������ʽƥ��
			HashMap<Integer, Province> resultmap;
			resultmap = sort(result);
			outResult(resultmap, provincelist, outPath);//������
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
            Province country = new Province("ȫ��", country_ip, country_sp, country_cure, country_dead);
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
		String pattern1 = "(\\S+) ���� ��Ⱦ���� (\\d+)��";
		String pattern2 = "(\\S+) ���� ���ƻ��� (\\d+)��";
		String pattern3 = "(\\S+) ��Ⱦ���� ���� (\\S+) (\\d+)��";
		String pattern4 = "(\\S+) ���ƻ��� ���� (\\S+) (\\d+)��";
		String pattern5 = "(\\S+) ���� (\\d+)��";
		String pattern6 = "(\\S+) ���� (\\d+)��";
		String pattern7 = "(\\S+) ���ƻ��� ȷ���Ⱦ (\\d+)��";
		String pattern8 = "(\\S+) �ų� ���ƻ��� (\\d+)��";
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
			if(!b) {//ʡ�ݲ�����
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
            if(!b) {//ʡ�ݲ�����
                Province p =new Province(matcher.group(1), 0, Integer.parseInt(matcher.group(2)), 0, 0);
                result.add(p);
            }
        }
		
		private void moveIP(ArrayList<Province> result, Matcher matcher) {
            int out = -1;//����ʡ
            int in = -1;//����ʡ
            for(int i = 0; i < result.size(); i++){
                if(result.get(i).getName().equals(matcher.group(1))){
                    out = i;
                }
                if(result.get(i).getName().equals(matcher.group(2))){
                    in = i;
                }
            }
            if(out == -1) {//����ʡ�ݲ�����
                System.out.println("����ʡ��" + matcher.group(1) + "�����ڸ�Ⱦ���ߣ���������");
            }
            else {
                //�޸�����ʡ�ĸ�Ⱦ��������
                if(in == -1) {//����ʡ�ݲ�����
                    Province p =new Province(matcher.group(2),
                            Integer.parseInt(matcher.group(3)), 0, 0, 0);
                    result.add(p);
                } else {
                    result.get(in).setIp(result.get(in).getIp() +
                            Integer.parseInt(matcher.group(3)));//�޸�����ʡ�ĸ�Ⱦ��������
                }
                result.get(out).setIp(result.get(out).getIp() -
                        Integer.parseInt(matcher.group(3)));//�޸�����ʡ�ĸ�Ⱦ��������
            }
        }

        private void moveSP(ArrayList<Province> result, Matcher matcher) {
            int out = -1;//����ʡ
            int in = -1;//����ʡ
            for(int i = 0; i < result.size(); i++){
                if(result.get(i).getName().equals(matcher.group(1))){
                    out = i;
                }
                if(result.get(i).getName().equals(matcher.group(2))){
                    in = i;
                }
            }
            if(out == -1) {//����ʡ�ݲ�����
                System.out.println("����ʡ��" + matcher.group(1) + "���������ƻ��ߣ���������");
            } else {
                //�޸�����ʡ�ĸ�Ⱦ��������
                if(in == -1) {//����ʡ�ݲ�����
                    Province p =new Province(matcher.group(2), 0,
                            Integer.parseInt(matcher.group(3)), 0, 0);
                    result.add(p);
                } else {
                    result.get(in).setSp(result.get(in).getSp() +
                            Integer.parseInt(matcher.group(3)));//�޸�����ʡ�ĸ�Ⱦ��������
                }
                result.get(out).setSp(result.get(out).getSp() -
                        Integer.parseInt(matcher.group(3)));//�޸�����ʡ�ĸ�Ⱦ��������
            }
        }

        private void addDead(ArrayList<Province> result, Matcher matcher) {
            boolean b = false;
            for (InfectStatistic.Province province : result) {
                if (province.getName().equals(matcher.group(1))) {
                    b = true;
                    province.setIp(province.getIp() - Integer.parseInt(matcher.group(2)));//�޸ĸ�ʡ�ݵĸ�Ⱦ��������
                    province.setDead(Integer.parseInt(matcher.group(2)) + province.getDead());//�޸ĸ�ʡ�ݵ���������
                }
            }
            if(!b) {//ʡ�ݲ�����
                System.out.println("����ʡ��" + matcher.group(1) + "�����ڸ�Ⱦ���ߣ���������");
            }
        }

        private void addCure(ArrayList<Province> result, Matcher matcher) {
            boolean b = false;
            for (InfectStatistic.Province province : result) {
                if (province.getName().equals(matcher.group(1))) {
                    b = true;
                    province.setIp(province.getIp() - Integer.parseInt(matcher.group(2)));//�޸ĸ�ʡ�ݵĸ�Ⱦ��������
                    province.setCure(Integer.parseInt(matcher.group(2)) + province.getCure());//�޸ĸ�ʡ�ݵ���������
                }
            }
            if(!b) {//ʡ�ݲ�����
                System.out.println("����ʡ��" + matcher.group(1) + "�����ڸ�Ⱦ���ߣ���������");
            }
        }

        private void diagnosisSp(ArrayList<Province> result, Matcher matcher) {
            boolean b = false;
            for (InfectStatistic.Province province : result) {
                if (province.getName().equals(matcher.group(1))) {
                    b = true;
                    province.setIp(Integer.parseInt(matcher.group(2)) + province.getIp());//�޸ĸ�ʡ�ݵĸ�Ⱦ��������
                    province.setSp(province.getSp() - Integer.parseInt(matcher.group(2)));//�޸ĸ�ʡ�ݵ����ƻ�������
                }
            }
            if(!b) {//ʡ�ݲ�����
                System.out.println("ȷ������ʡ��" + matcher.group(1) + "���������ƻ��ߣ���������");
            }
        }

        private void excludeSp(ArrayList<Province> result, Matcher matcher) {
            boolean b = false;
            for (InfectStatistic.Province province : result) {
                if (province.getName().equals(matcher.group(1))) {
                    b = true;
                    province.setSp(province.getSp() - Integer.parseInt(matcher.group(2)));//�޸ĸ�ʡ�ݵ����ƻ�������
                }
            }
            if(!b) {//ʡ�ݲ�����
                System.out.println("ȷ������ʡ��" + matcher.group(1) + "���������ƻ��ߣ���������");
            }
        }
		
        private void outResult(HashMap<Integer, Province> result_map,
                ArrayList<String> provincelist, String outpath) throws IOException {
        		
        				initFile(outpath);
        				FileWriter fw = new FileWriter(outpath, true);
        				BufferedWriter bw = new BufferedWriter(fw);
        				Set<Entry<Integer, Province>> entries =result_map.entrySet();
        				if(provincelist != null && !provincelist.isEmpty()) {//province�в���ֵ
        					for(Entry<Integer, Province> entry:entries){
        						if(provincelist.contains(provinceStr[entry.getKey()])) {
        							bw.write(entry.getValue().printResult());
        							try {
										bw.write("\n");
									} catch (IOException e) {
										// TODO �Զ����ɵ� catch ��
										e.printStackTrace();
									}//����
        						}
        					}
        				} else {
        					for(Entry<Integer, Province> entry:entries ){
        						try {
									bw.write(entry.getValue().printResult());
								} catch (IOException e1) {
									// TODO �Զ����ɵ� catch ��
									e1.printStackTrace();
								}
        						try {
									bw.write("\n");
								} catch (IOException e) {
									// TODO �Զ����ɵ� catch ��
									e.printStackTrace();
								}//����
        					}
        				}
        				bw.write("// ���ĵ�������ʵ���ݣ���������ʹ��");
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
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}

			
		}

		public void writeOutTxt() {  //����ļ�����
			FileWriter fwriter = null;
			int i, j;	
			provinceExist[0] = 1; 
			try {
				fwriter = new FileWriter(outPath);  
				if(isProvinceExist == 0){  //��-provinceδָ��
					for(i = 0; i < 35; i++){
						if(provinceExist[i] == 1){
							fwriter.write(provinceStr[i] + " ");
							if(isTypeExist == 0){  //��-typeδָ��
								for( j = 0; j < 4; j++)
									fwriter.write(typeStr[j] + peopleNumber[i][j] + "�� ");}
							
						
							else{  //��-typeָ��
								for( j = 0; j < 4; j++)
									if(type[j] != 0)
									fwriter.write(typeStr[type[j]-1] + peopleNumber[i][type[j]-1] + "�� ");
							}
							fwriter.write("\n");
						}
					}
				}
				else {  //��-province��ָ��
					for(i = 0; i < 35; i++){
						if(province[i] == 1){
							fwriter.write(provinceStr[i] + " ");
							if(isTypeExist == 0){  //��-typeδָ��
								for( j = 0; j < 4; j++)
									fwriter.write(typeStr[j] + peopleNumber[i][j] + "�� ");
							}
							else{  //��-typeָ��
								for( j = 0; j < 4; j++)
									if(type[j] != 0)
										fwriter.write(typeStr[type[j]-1] + peopleNumber[i][type[j]-1] + "�� ");
							} 
							fwriter.write("\n");
						}
							
					}
				}
				        
				fwriter.write("// ���ĵ�������ʵ���ݣ���������ʹ��");
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
