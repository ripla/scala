package scala.reflect
package macros

trait Infrastructure {
  self: Context =>

  /** Determines whether the compiler expanding a macro targets JVM.
   */
  val forJVM: Boolean

  /** Determines whether the compiler expanding a macro targets CLR.
   */
  val forMSIL: Boolean

  /** Determines whether the compiler expanding a macro is a presentation compiler.
   */
  val forInteractive: Boolean

  /** Determines whether the compiler expanding a macro is a Scaladoc compiler.
   */
  val forScaladoc: Boolean

  /** Exposes current compilation run.
   */
  val currentRun: Run

  /** Exposes library classpath.
   */
  val libraryClassPath: List[java.net.URL]

  /** Exposes a classloader that corresponds to the library classpath.
   *
   *  With this classloader you can perform on-the-fly evaluation of macro arguments.
   *  For example, consider this code snippet:
   *
   *    def staticEval[T](x: T) = macro staticEval[T]
   *
   *    def staticEval[T](c: Context)(x: c.Expr[T]) = {
   *      import scala.reflect.runtime.{universe => ru}
   *      val mirror = ru.runtimeMirror(c.libraryClassLoader)
   *      import scala.tools.reflect.ToolBox
   *      val toolBox = mirror.mkToolBox()
   *      val importer = ru.mkImporter(c.universe).asInstanceOf[ru.Importer { val from: c.universe.type }]
   *      val tree = c.resetAllAttrs(x.tree.duplicate)
   *      val imported = importer.importTree(tree)
   *      val valueOfX = toolBox.eval(imported).asInstanceOf[T]
   *      ...
   *    }
   */
  def libraryClassLoader: ClassLoader

  /** As seen by macro API, compilation run is an opaque type that can be deconstructed into:
   *    1) Current compilation unit
   *    2) List of all compilation units that comprise the run
   */
  type Run

  val Run: RunExtractor

  abstract class RunExtractor {
    def unapply(run: Run): Option[(CompilationUnit, List[CompilationUnit])]
  }

  /** As seen by macro API, compilation unit is an opaque type that can be deconstructed into:
   *    1) File that corresponds to the unit (if not applicable, null)
   *    2) Content of the file (if not applicable, empty array)
   *    3) Body, i.e. the AST that represents the compilation unit
   */
  type CompilationUnit

  val CompilationUnit: CompilationUnitExtractor

  abstract class CompilationUnitExtractor {
    def unapply(compilationUnit: CompilationUnit): Option[(java.io.File, Array[Char], Tree)]
  }

  /** Returns a macro definition which triggered this macro expansion.
   */
  val currentMacro: Symbol
}
