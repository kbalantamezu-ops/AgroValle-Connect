<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">

<!-- Reglas de estilo basadas en Google Java Style, adaptadas para AgroValle Connect -->
<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="warning"/>
    <property name="fileExtensions" value="java"/>

    <module name="LineLength">
        <property name="max" value="120"/>
    </module>

    <module name="FileTabCharacter"/>

    <module name="TreeWalker">
        <!-- Nombres -->
        <module name="ClassTypeParameterName"/>
        <module name="MethodName"/>
        <module name="LocalVariableName"/>
        <module name="MemberName"/>
        <module name="ParameterName"/>
        <module name="StaticVariableName"/>
        <module name="TypeName"/>

        <!-- Imports -->
        <module name="AvoidStarImport"/>
        <module name="UnusedImports"/>
        <module name="RedundantImport"/>

        <!-- Espaciado y formato -->
        <module name="WhitespaceAround"/>
        <module name="EmptyLineSeparator"/>
        <module name="Indentation">
            <property name="basicOffset" value="4"/>
        </module>

        <!-- Llaves -->
        <module name="NeedBraces"/>
        <module name="LeftCurly"/>
        <module name="RightCurly"/>

        <!-- Buenas prácticas -->
        <module name="EqualsHashCode"/>
        <module name="SimplifyBooleanExpression"/>
        <module name="SimplifyBooleanReturn"/>
        <module name="MagicNumber">
            <property name="ignoreNumbers" value="0, 1, -1"/>
        </module>
        <module name="EmptyCatchBlock"/>

        <!-- Documentación -->
        <module name="JavadocMethod">
            <property name="accessModifiers" value="public"/>
        </module>
    </module>
</module>
