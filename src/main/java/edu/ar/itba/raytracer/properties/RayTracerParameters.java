package edu.ar.itba.raytracer.properties;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters
public class RayTracerParameters {
    @Parameter(names = "-i", required = true, description = "Nombre del archivo de entrada (definición de la escena)")
    public String input;
    @Parameter(names = "-o", description = "Nombre del archivo de salida, incluyendo su extensión. "
            + "En caso de no indicarlo usará el nombre del archivo de input reemplazando la extensión "
            + "y usando el formato PNG.")
    public String output = input;
    @Parameter(names = "-time", description = "Mostrará el tiempo empleado en el render")
    public boolean time = false;
    @Parameter(names = "-aa",  description = "Cantidad de muestras de antialiasing.")
    public int aaSamples = 1;
    @Parameter(names = "-benchmark", description = "Realizar el render completo n veces consecutivas.")
    public int benchmark = 1;
    @Parameter(names = "-d", description = "Define el ray depth de reflejos y refracciones.")
    public int rayDepth = 1;
    @Parameter(names="-pathtracer", description = "Define si se utiliza o no el path tracer.")
    public boolean pathTracer = false;
    @Parameter(names="-tn", description = "Define el trace depth del path tracer.")
    public int traceDepth = 5;
    @Parameter(names="-s", description = "Define la cantidad de samples por pixel.")
    public int traceSamples = 20;
    @Parameter(names="-nodirect", description = "No se calcula la iluminación directa.", arity = 1, hidden = true)
    public boolean direct = true;
    @Parameter(names="-noindirect", description = "No se calcula la iluminación directa.", arity = 1, hidden = true)
    public boolean indirect = true;
}
