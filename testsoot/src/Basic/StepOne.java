package Basic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class StepOne {
	public static List<String> onetoone = new ArrayList<>();

	private String packagename1;
	private String packagename2;

	private AnalysisBasic analysisbasic1;
	private AnalysisBasic analysisbasic2;

	public StepOne(String packagename1, String packagename2,AnalysisBasic analysisBasic1,AnalysisBasic analysisbasic2) throws IOException, ClassNotFoundException {

		this.packagename1 = packagename1;
		this.packagename2 = packagename2;

		this.analysisbasic1=analysisBasic1;
		this.analysisbasic2=analysisbasic2;

		findOneToOne();

//		for (String stemp : onetoone) {
//			System.out.println("完全相同的（类<->类）对：" + stemp);
//		}
		
		

	}

	public void findOneToOne() {
		int n = 2;

			for (Class ctemp2 : analysisbasic2.classlist) {
				boolean flag1, flag2, flag3;
				flag1 = flag2 = flag3 = false;

				int fnum = 0;
				int mnum = 0;

				String name = "";
				for (Class ctemp1 : analysisbasic1.classlist) {

					flag1 = flag2 = flag3 = false;

//					int i2 = ctemp2.classname.lastIndexOf(".") + 1;
//					int i1 = ctemp1.classname.lastIndexOf(".") + 1;
					// System.out.println("版本2：" +
					// ctemp2.classname.substring(i2));
					// System.out.println("版本1：" +
					// ctemp1.classname.substring(i1));
					// 类名
//					if (ctemp2.classname.substring(i2).equals(
//							ctemp1.classname.substring(i1))) {
//						flag1 = true;
//
//					}
                    if (ctemp2.classname.equals(
                            ctemp1.classname)) {
                        flag1 = true;

                    }

					// 属性//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					if (ctemp2.fields.isEmpty() && ctemp1.fields.isEmpty()) {
						flag2 = true;
					} else if ((ctemp2.fields.isEmpty() && !ctemp1.fields
							.isEmpty())
							|| (!ctemp2.fields.isEmpty() && ctemp1.fields
									.isEmpty())) {
						flag2 = false;
						continue;
					} else {
						for (Class.Field ftemp2 : ctemp2.fields) {

//							String ftemp2type = "ftemp2type";
//							String ftemp1type = "ftemp1type";

                            String ftemp2type = ftemp2.type;


							boolean b = false;
							// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//							if (ftemp2.type.contains(packagename2)) {
//								int ii2 = ftemp2.type.lastIndexOf(".") + 1;
//								ftemp2type = ftemp2.type.substring(ii2);
//
//							}
							// /////////////////////////////////////////////////////////////////////////////////////////////////////////////
							for (Class.Field ftemp1 : ctemp1.fields) {
								// /////////////////////////////////////////////////////////////////////////////////////////////////////////
//								if (ftemp1.type.contains(packagename1)) {
//									int ii1 = ftemp1.type.lastIndexOf(".") + 1;
//									ftemp1type = ftemp1.type.substring(ii1);
//
//								}
                                String ftemp1type = ftemp1.type;
								// /////////////////////////////////////////////////////////////////////////////////////////////////////////
								if (ftemp1type.equals(ftemp2type)) {
									b = true;
								} else {

									String ftemp2type2 = "";
									String ftemp1type2 = "";

									String ftemp2type3 = "";
									String ftemp1type3 = "";

									ftemp2type2 = ftemp2type.replace(
											"ArrayList<", "");
									ftemp2type2 = ftemp2type2.replace(" >", "");
									ftemp2type2 = ftemp2type2.replace("[]", "");
									ftemp1type2 = ftemp1type.replace(
											"ArrayList<", "");
									ftemp1type2 = ftemp1type2.replace(" >", "");
									ftemp1type2 = ftemp1type2.replace("[]", "");

									ftemp2type3 = ftemp2type.replace(
											ftemp2type2, "");
									ftemp1type3 = ftemp1type.replace(
											ftemp1type2, "");

									// 处理ArrayList<类>和类[]
									if (ftemp2type.equals(ftemp1type)
											&& ftemp2type3.equals(ftemp1type3)) {
										b = true;
									}
								}
								// //////////////////////////////////////////////////////////////////////////////////////////////////
								if (ftemp2.modifiers.equals(ftemp1.modifiers)
										&& (ftemp2.type.equals(ftemp1.type) || b == true)
										&& ftemp2.name.equals(ftemp1.name)) {
									fnum++;
									if (fnum == ctemp2.fields.size()) {
										flag2 = true;
										break;
									}
								}
							}
						}
					}

					// 方法//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					if (ctemp2.methods.isEmpty() && ctemp1.methods.isEmpty()) {
						flag3 = true;
					} else if ((ctemp2.methods.isEmpty() && !ctemp1.methods
							.isEmpty())
							|| (!ctemp2.methods.isEmpty() && ctemp1.methods
									.isEmpty())) {
						flag3 = false;
						continue;
					} else {
						for (Class.Method mtemp2 : ctemp2.methods) {
//							String mtemp2returntype = "mtemp2returntype";
//							String mtemp1returntype = "mtemp1returntype";

                            String mtemp2returntype = mtemp2.returntype;


							String[] mtemp2paramtype = null;
							String[] mtemp1paramtype = null;

							boolean d = false;
							boolean c = false;
							// //////////////////////////////////////////////////////////////////////////////////////////////////////////
//							if (mtemp2.returntype.contains(packagename2)) {
//								int ii2 = mtemp2.returntype.lastIndexOf(".") + 1;
//								mtemp2returntype = mtemp2.returntype
//										.substring(ii2);
//
//							}

							int xiabiao2 = 0;
							mtemp2paramtype = new String[mtemp2.paramtypes
									.size()];
							for (String ptemp2 : mtemp2.paramtypes) {
//								if (ptemp2.contains(packagename2)) {
//									int iii2 = ptemp2.lastIndexOf(".") + 1;
//									mtemp2paramtype[xiabiao2] = ptemp2
//											.substring(iii2);
//								} else {
									mtemp2paramtype[xiabiao2] = ptemp2;
//								}
								xiabiao2++;
							}
							// //////////////////////////////////////////////////////////////////////////////////////////////////////////
							for (Class.Method mtemp1 : ctemp1.methods) {
								// /////////////////////////////////////////////////////////////////////////////////////////////////////////

                                String mtemp1returntype = mtemp1.returntype;

//                                if (mtemp1.returntype.contains(packagename1)) {
//									int ii1 = mtemp1.returntype
//											.lastIndexOf(".") + 1;
//									mtemp1returntype = mtemp1.returntype
//											.substring(ii1);
//
//								}

								int xiabiao1 = 0;
								mtemp1paramtype = new String[mtemp1.paramtypes
										.size()];
								for (String ptemp1 : mtemp1.paramtypes) {
//									if (ptemp1.contains(packagename1)) {
//										int iii1 = ptemp1.lastIndexOf(".") + 1;
//										mtemp1paramtype[xiabiao1] = ptemp1
//												.substring(iii1);
//									} else {
										mtemp1paramtype[xiabiao1] = ptemp1;
//									}
									xiabiao1++;
								}
								// ///////////////////////////////////////////////////////////////////////////////////////////////////////
								if (mtemp1returntype.equals(mtemp2returntype)) {
									d = true;
								} else {

									String mtemp2returntype2 = "";
									String mtemp1returntype2 = "";

									String mtemp2returntype3 = "";
									String mtemp1returntype3 = "";

									mtemp2returntype2 = mtemp2returntype
											.replace("ArrayList<", "");
									mtemp2returntype2 = mtemp2returntype2
											.replace(" >", "");
									mtemp2returntype2 = mtemp2returntype2
											.replace("[]", "");
									mtemp1returntype2 = mtemp1returntype
											.replace("ArrayList<", "");
									mtemp1returntype2 = mtemp1returntype2
											.replace(" >", "");
									mtemp1returntype2 = mtemp1returntype2
											.replace("[]", "");

									mtemp2returntype3 = mtemp2returntype
											.replace(mtemp2returntype2, "");
									mtemp1returntype3 = mtemp1returntype
											.replace(mtemp1returntype2, "");

									// 处理ArrayList<类>和类[]
									if (mtemp1returntype2
											.equals(mtemp2returntype2)
											&& mtemp2returntype3
													.equals(mtemp1returntype3)) {
										d = true;
									}
								}

								int size = 0;
								if (mtemp2.paramtypes.isEmpty()
										&& mtemp1.paramtypes.isEmpty()) {
									c = true;

								} else if ((!mtemp2.paramtypes.isEmpty() && mtemp1.paramtypes
										.isEmpty())
										|| (mtemp2.paramtypes.isEmpty() && !mtemp1.paramtypes
												.isEmpty())) {
									c = false;
									continue;
								} else {

									if (mtemp2paramtype.length != mtemp1paramtype.length) {
										c = false;
										continue;
									} else {
										for (int i = 0; i < mtemp2paramtype.length; i++) {
											if (mtemp2paramtype[i]
													.equals(mtemp1paramtype[i])) {
												size++;
											} else {
												String mtemp2paramtype2 = "";
												String mtemp1paramtype2 = "";

												String mtemp2paramtype3 = "";
												String mtemp1paramtype3 = "";

												// 处理ArrayList<类>和类[]
												mtemp2paramtype2 = mtemp2paramtype[i]
														.replace("ArrayList<",
																"");
												mtemp2paramtype2 = mtemp2paramtype2
														.replace(" >", "");
												mtemp2paramtype2 = mtemp2paramtype2
														.replace("[]", "");
												mtemp1paramtype2 = mtemp1paramtype[i]
														.replace("ArrayList<",
																"");
												mtemp1paramtype2 = mtemp1paramtype2
														.replace(" >", "");
												mtemp1paramtype2 = mtemp1paramtype2
														.replace("[]", "");

												mtemp2paramtype3 = mtemp2paramtype[i]
														.replace(
																mtemp2paramtype2,
																"");
												mtemp1paramtype3 = mtemp1paramtype[i]
														.replace(
																mtemp1paramtype2,
																"");

												if (mtemp1paramtype2
														.equals(mtemp2paramtype2)
														&& mtemp2paramtype3
																.equals(mtemp1paramtype3)) {
													size++;
												}
											}
										}
										if (size == mtemp2paramtype.length) {
											c = true;
										}
									}
								}

								if (mtemp2.modifiers.equals(mtemp1.modifiers)
										&& (mtemp2.returntype
												.equals(mtemp1.returntype) || d == true)
										&& mtemp2.name.equals(mtemp1.name)
										&& (mtemp2.paramtypes
												.equals(mtemp1.paramtypes) || c == true)) {
									mnum++;
									if (mnum == ctemp2.methods.size()) {
										flag3 = true;
										break;
									}
								}
							}
						}
					}
					// System.out.println(flag1 + " " + flag2 + " " + flag3);
					if (flag1 == true && flag2 == true && flag3 == true) {
						name = ctemp1.classname;
						break;
					}
				}
				if (flag1 == true && flag2 == true && flag3 == true) {
					String relation = name + "<->" + ctemp2.classname;
					onetoone.add(relation);
				}
			}
		}
	}
