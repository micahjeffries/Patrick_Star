import processing.core.PApplet;
import java.util.Optional;
import processing.core.PImage;

/*
WorldView ideally mostly controls drawing the current part of the whole world
that we can see based on the viewport
*/

final class WorldView
{

   private PApplet screen;
   private WorldModel world;
   private int tileWidth;
   private int tileHeight;
   private Viewport viewport;

   public WorldView(int numRows, int numCols, PApplet screen, WorldModel world,
      int tileWidth, int tileHeight)
   {
      this.screen = screen;
      this.world = world;
      this.tileWidth = tileWidth;
      this.tileHeight = tileHeight;
      this.viewport = new Viewport(numRows, numCols);
   }

   private static int clamp(int value, int low, int high)
   {
      return Math.min(high, Math.max(value, low));
   }

   public void shiftView(int colDelta, int rowDelta)
   {
      int newCol = this.clamp(this.viewport.getColumn() + colDelta, 0,
              this.world.getNumCols() - this.viewport.getNumColumns());
      int newRow = this.clamp(this.viewport.getRow() + rowDelta, 0,
              this.world.getNumRows() - this.viewport.getNumRows());

      this.viewport.shift(newCol, newRow);
   }

   private void drawBackground()
   {
      for (int row = 0; row < this.viewport.getNumRows(); row++)
      {
         for (int col = 0; col < this.viewport.getNumColumns(); col++)
         {
            Point worldPoint = this.viewport.viewportToWorld(col, row);
            Optional<PImage> image = worldPoint.getBackgroundImage(this.world);
            if (image.isPresent())
            {
               this.screen.image(image.get(), col * this.tileWidth,
                       row * this.tileHeight);
            }
         }
      }
   }

   private void drawEntities()
   {
      for (Entity entity : this.world.getEntities())
      {
         Point pos = entity.getPosition();

         if (this.viewport.contains(pos))
         {
            Point viewPoint = this.viewport.worldToViewport(pos.getX(), pos.getY());
            this.screen.image(entity.getCurrentImage(),
                    viewPoint.getX() * this.tileWidth, viewPoint.getY() * this.tileHeight);
         }


      }
   }

   public void drawViewport()
   {
      this.drawBackground();
      this.drawEntities();
   }



}
