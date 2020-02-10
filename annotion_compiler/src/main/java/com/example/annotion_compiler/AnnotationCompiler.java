package com.example.annotion_compiler;

import com.example.annotion.BindPath;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * 注解处理器
 */
@AutoService(Processor.class)//注册注解处理器
public class AnnotationCompiler extends AbstractProcessor {
    //生成java文件的对象
    Filer filer;

    //初始化1
    // 初始化好filer
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    /**
     * 初始化2
     * 声明 注解 处理器支持的java版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    /**
     * 初始化3
     * 声明注解处理器：要处理的注解
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindPath.class.getCanonicalName());//拿到名字
        return types;
    }

    /**
     * 写文件
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //1.获取到整个模块用所有用到了bindpath注解的节点(包括类节点，方法节点，成员变量节点）
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(BindPath.class);
        //2.结构化数据的容器，即实现如下代码 ：Arouter.newInstance().addActivity("login",LoginActivity.class);
        Map<String, String> map = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {//遍历所有的节点
            //因为bindpath注解放在类节点上 所以我们这里获取到的都是类节点
            TypeElement typeElement = (TypeElement) element;
            //通过类节点获取到它上面的注解
            BindPath annotation = typeElement.getAnnotation(BindPath.class);
            String key = annotation.value();//获取到注解的值，即 login/login
            //获取类对象的包名加类名
            String activityName = typeElement.getQualifiedName().toString();
            map.put(key, activityName);
        }
        //3.写文件
        if (map.size() > 0) {
            //创建一个文件名 app，login，mine每个模块都会执行一次，即一共执行3次，要保证每个文件名不重复
            String utilName = "ActivityUtil" + System.currentTimeMillis();
            Writer writer = null;
            try {
                //生成文件
                //创建源码目录
                JavaFileObject sourceFile = filer.createSourceFile("com.luuuzi.util." + utilName);
                writer = sourceFile.openWriter();
                writer.write("package com.luuuzi.util;\n" +
                        "\n" +
                        "import com.example.arouter.Arouter;\n" +
                        "import com.example.arouter.IRouter;\n" +
                        "//将loginActivity添加进去\n" +
                        "public class " + utilName + " implements IRouter {\n" +
                        "    @Override\n" +
                        "    public void putActivity() {");
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = map.get(key);
                    writer.write("Arouter.newInstance().addActivity(\"" + key + "\"," + value + ".class);\n");
                }
                writer.write("}\n}");

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (writer!=null){
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return false;
    }
}
