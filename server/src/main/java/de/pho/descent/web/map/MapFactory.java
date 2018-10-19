package de.pho.descent.web.map;

import de.pho.descent.shared.model.map.GameMap;
import de.pho.descent.shared.model.map.MapField;
import de.pho.descent.shared.model.map.MapTileGroup;
import de.pho.descent.shared.model.quest.QuestTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author pho
 */
public class MapFactory {

    private static final String MAPS_FILE_PATH = "/quest_maps/maps.xlsx";
    private static final String MAP_IMAGE_PATH = "/img/encounter/";

    public static GameMap createMapByTemplate(QuestTemplate template) throws IOException {
        Map<MapTileGroup, List<MapField>> mapFields = new HashMap<>();
        XSSFWorkbook maps = getWorkbook();
        XSSFSheet mapSheet = maps.getSheet(template.getQuest().name() + "_" + template.getQuestPart().name());

        for (int y = 0; y < template.getGridYSize(); y++) {
            XSSFRow row = mapSheet.getRow(y);
            for (int x = 0; x < template.getGridXSize(); x++) {
                XSSFCell cell = row.getCell(x);
                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                    String[] values = cell.getStringCellValue().split(",");
                    String mapTileGroup = values[0];
                    String moveCost = values[1];

                    boolean passable = true;
                    boolean herospawn = false;
                    boolean monsterspawn = false;

//                    short colorindex = cell.getCellStyle().getFillBackgroundColor();
//                    short colorindex2 = cell.getCellStyle().getFillForegroundColor();
//                    short blue = HSSFColor.HSSFColorPredefined.BLUE.getIndex();
//                    if ((cell.getColumnIndex() % 2) == 0) {
//                        cell.getCellStyle().setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
//                    } else {
//                        cell.getCellStyle().setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
//                    }
//                    XSSFColor color = cell.getCellStyle().getFont().getXSSFColor();
                    if (cell.getCellStyle().getFillForegroundColor() == HSSFColor.HSSFColorPredefined.GREEN.getIndex()) {
                        herospawn = true;
                    } else if (cell.getCellStyle().getFillForegroundColor() == HSSFColor.HSSFColorPredefined.BLUE.getIndex()) {
                        monsterspawn = true;
                    } else if (cell.getCellStyle().getFillForegroundColor() == HSSFColor.HSSFColorPredefined.RED.getIndex()) {
                        passable = false;
                    }

                    // add map field
                    MapTileGroup group = new MapTileGroup(mapTileGroup);
                    MapField field = new MapField(group, null, null,
                            cell.getColumnIndex(), cell.getRowIndex(),
                            Integer.parseInt(moveCost), passable, herospawn, monsterspawn);
                    if (mapFields.containsKey(group)) {
                        List<MapField> fields = mapFields.get(group);
                        fields.add(field);
                    } else {
                        List<MapField> fields = new ArrayList<>();
                        fields.add(field);
                        mapFields.put(group, fields);
                    }
                }
            }
        }
        // Write the output to a file
//        try (OutputStream fileOut = new FileOutputStream("workbook.xlsx")) {
//            maps.write(fileOut);
//        }

        // close workbook, not needed anymore
        maps.close();

        GameMap map = new GameMap();
        map.setQuest(template.getQuest());
        map.setPart(template.getQuestPart());
        map.setGridXSize(template.getGridXSize());
        map.setGridYSize(template.getGridYSize());
        map.setImagePath(MAP_IMAGE_PATH + template.getQuest().name() + "_" + template.getQuestPart().name() + ".png");

        for (Map.Entry<MapTileGroup, List<MapField>> entry : mapFields.entrySet()) {
            map.getMapFields().addAll(entry.getValue());
        }

        return map;
    }

    private static XSSFWorkbook getWorkbook() throws IOException {
        XSSFWorkbook book = null;

        try {
            book = new XSSFWorkbook(MapFactory.class.getResourceAsStream(MAPS_FILE_PATH));
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(MapFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return book;
    }
}
