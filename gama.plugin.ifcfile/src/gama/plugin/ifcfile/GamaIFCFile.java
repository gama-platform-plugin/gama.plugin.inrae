/*********************************************************************************************
 *
 * 'GamaDXFFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.plugin.ifcfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;

import gama.annotations.constants.IKeyword;
import gama.annotations.support.IConcept;
import gama.api.GAMA;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.GamaGeometryType;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.Types;
import gama.api.runtime.scope.IScope;
import gama.api.types.geometry.GamaPoint;
import gama.api.types.geometry.GamaPointFactory;
import gama.api.types.geometry.GamaShapeFactory;
import gama.api.types.geometry.IPoint;
import gama.api.types.geometry.IShape;
import gama.api.types.list.GamaListFactory;
import gama.api.types.list.IList;
import gama.api.types.map.GamaMapFactory;
import gama.api.types.map.IMap;
import gama.api.utils.geometry.GeometryUtils;
import gama.api.utils.geometry.IEnvelope;
import gama.core.util.file.GamaGeometryFile;
import gama.gaml.operators.spatial.SpatialCreation;
import gama.gaml.operators.spatial.SpatialTransformations;
import ifc2x3javatoolbox.ifc2x3tc1.IfcArbitraryClosedProfileDef;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement2D;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis2Placement3D;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBooleanClippingResult;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBooleanOperand;
import ifc2x3javatoolbox.ifc2x3tc1.IfcCartesianPoint;
import ifc2x3javatoolbox.ifc2x3tc1.IfcCurve;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDirection;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDoor;
import ifc2x3javatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import ifc2x3javatoolbox.ifc2x3tc1.IfcLocalPlacement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterial;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialLayer;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialLayerSet;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialLayerSetUsage;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialSelect;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObjectDefinition;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObjectPlacement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcOpeningElement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPolyline;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProduct;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProperty;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPropertySet;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPropertySetDefinition;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPropertySingleValue;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRectangleProfileDef;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelAssociates;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelAssociatesMaterial;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDecomposes;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefines;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefinesByProperties;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefinesByType;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRepresentation;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRepresentationItem;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRoof;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSlab;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSpace;
import ifc2x3javatoolbox.ifc2x3tc1.IfcTypeObject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWall;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWindow;
import ifc4javatoolbox.ifcmodel.IfcModel;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@gama.annotations.file (
		name = "ifc",
		extensions = { "ifc" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { "ifc", IConcept.FILE })
public class GamaIFCFile extends GamaGeometryFile {

	public GamaIFCFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	protected IShape buildGeometry(final IScope scope) {
		return GamaShapeFactory.geometriesToGeometry(scope, getBuffer());
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO are there attributes ?
		return GamaListFactory.create();
	}

	public IPoint toPoint(final IfcDirection direction) {
		if (direction != null) {
			if (direction.getDirectionRatios().size() > 2) {
				return  GamaPointFactory.create(direction.getDirectionRatios().get(0).value,
						direction.getDirectionRatios().get(1).value, direction.getDirectionRatios().get(2).value);
			}
			return GamaPointFactory.create(direction.getDirectionRatios().get(0).value,
					direction.getDirectionRatios().get(1).value);
		} 
		return null;
	}

	public IPoint toPoint(final IfcCartesianPoint point) {
		if (point != null) {
			if (point.getCoordinates().size() > 2) {
				return GamaPointFactory.create(point.getCoordinates().get(0).value, point.getCoordinates().get(1).value,
						point.getCoordinates().get(2).value);
			}
			return GamaPointFactory.create(point.getCoordinates().get(0).value, point.getCoordinates().get(1).value);
		}
		return null;
	}

	public class Axe {
		public IPoint origin;
		IPoint xDir;
		IPoint yDir;
		IPoint zDir;

		public Axe() {
			origin = GamaPointFactory.create(0, 0, 0);
			xDir = GamaPointFactory.create(1, 0, 0);
			yDir = GamaPointFactory.create(0, 1, 0); 
			zDir =GamaPointFactory.create(0, 0, 1);
		}

		public Axe(final Axe pa) {
			origin = GamaPointFactory.create(pa.origin);
			xDir = GamaPointFactory.create(pa.xDir);
			yDir = GamaPointFactory.create(pa.yDir);
			zDir =GamaPointFactory.create(pa.zDir);
		}

		public IPoint toNewRef(final IPoint pt, final boolean normalize) {
			double x = pt.getX() * xDir.getX() + pt.getY() * yDir.getX() + pt.getZ() * zDir.getX();
			double y =  pt.getX() * xDir.getY() + pt.getY() * yDir.getY() + pt.getZ() * zDir.getY();
			double z =  pt.getX() * xDir.getZ() + pt.getY() * yDir.getZ() + pt.getZ() * zDir.getZ();
			if (normalize) {
				final double dist = Math.sqrt(x * x + y * y + z * z);
				x /= dist;
				y /= dist;
				z /= dist; 
			}
			return  GamaPointFactory.create(x, y, z);
		}
		
		public IPoint toNewRef(final Coordinate pt, final boolean normalize) {
			double x = pt.getX() * xDir.getX() + pt.getY() * yDir.getX() + pt.getZ() * zDir.getX();
			double y =  pt.getX() * xDir.getY() + pt.getY() * yDir.getY() + pt.getZ() * zDir.getY();
			double z =  pt.getX() * xDir.getZ() + pt.getY() * yDir.getZ() + pt.getZ() * zDir.getZ();
			if (normalize) {
				final double dist = Math.sqrt(x * x + y * y + z * z);
				x /= dist;
				y /= dist;
				z /= dist;
			}
			return  GamaPointFactory.create(x, y, z);
		}

		public void addTranslation(final IPoint transl) {
			final IPoint newPt = toNewRef(transl, false); 
			origin = GamaPointFactory.create(origin.getX()+newPt.getX(),origin.getY()+newPt.getY(),origin.getZ()+newPt.getZ());
		}

		public void addRotation(final IPoint xVector) {
			xDir = toNewRef(xVector, true);
			yDir = (GamaPoint) SpatialTransformations.rotated_by(GAMA.getRuntimeScope(), xVector, 90).getLocation();
		}

		public void addRotation(final IPoint xVector, final IPoint zVector) {
			xDir = toNewRef(xVector, true);
			zDir = toNewRef(zVector, true);
			yDir = GamaPointFactory.create(-1 * (xDir.getY() * zDir.getZ() - xDir.getZ() * zDir.getY()), -1 * (xDir.getZ() * zDir.getX() - xDir.getX() * zDir.getZ()),
					-1 * (xDir.getX() * zDir.getY() - xDir.getY() * zDir.getX()));
		}

		public void transform(final IShape shape) {
			shape.getInnerGeometry().apply((final Coordinate p) -> {
				final IPoint np = toNewRef(p, false);
				p.x = np.getX() + origin.getX();
				p.y = np.getY() + origin.getY();
				p.z = np.getZ() + origin.getZ();
			});
		}

		public void update(final List<IfcAxis2Placement> axispls, final boolean reverse) {
			if (reverse) {
				Collections.reverse(axispls);
			}
			for (final IfcAxis2Placement ap : axispls) {
				update(ap);
			}
		}

		public void update(final IfcAxis2Placement axispl) {
			if (axispl instanceof IfcAxis2Placement2D) {
				final IfcAxis2Placement2D axispl2D = (IfcAxis2Placement2D) axispl;
				final IPoint loc = toPoint(axispl2D.getLocation());
				addTranslation(loc);

				if (axispl2D.getRefDirection() != null) {
					final IPoint dir = toPoint(axispl2D.getRefDirection());
					addRotation(dir);
				}
			} else if (axispl instanceof IfcAxis2Placement3D) {
				final IfcAxis2Placement3D axispl3D = (IfcAxis2Placement3D) axispl;
				final IPoint loc = toPoint(axispl3D.getLocation());
				addTranslation(loc);

				if (axispl3D.getRefDirection() != null) {
					final IPoint dir = toPoint(axispl3D.getRefDirection());
					final IPoint axis = toPoint(axispl3D.getAxis());
					addRotation(dir, axis);
				}
			}
		}

		@Override
		public String toString() {
			return "Axe [origin=" + origin + ", xDir=" + xDir + ", yDir=" + yDir + ", zDir=" + zDir + "]";
		}

	}

	public IShape toGeom(final IScope scope, final Collection<IfcCartesianPoint> line, final boolean polygon) {
		final List<IShape> pts = new ArrayList<>();
		for (final IfcCartesianPoint pt : line) {
			pts.add(toPoint(pt));
		}
		return polygon ?  GamaShapeFactory.buildPolygon(pts) : GamaShapeFactory.buildPolyline(pts);
	}

	public IShape createOpening(final IScope scope, final IfcOpeningElement o) {
		if (o.getObjectPlacement() == null) { return null; }
		final Axe newAxe = new Axe();
		final List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, o.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		final IShape box = SpatialCreation.sphere(scope, 0.2);
		addAttribtutes(o, box);
		newAxe.transform(box);

		return box;
		/*
		 * if (o.getObjectPlacement() == null) return null; final Axe newAxe = new Axe(); final List<IfcAxis2Placement>
		 * aps = new ArrayList<>(); relatedTo(scope, o.getObjectPlacement(), aps); newAxe.update(aps, true); for (final
		 * IfcRepresentation rep : o.getRepresentation().getRepresentations()) { for (final IfcRepresentationItem item :
		 * rep.getItems()) { if (item instanceof IfcExtrudedAreaSolid) { final IfcExtrudedAreaSolid solid =
		 * (IfcExtrudedAreaSolid) item; if (solid.getPosition() != null) { newAxe.update(solid.getPosition()); } final
		 * Double depth = solid.getDepth().value; if (solid.getSweptArea() instanceof IfcRectangleProfileDef) { final
		 * IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea(); final Double height =
		 * profil.getXDim().value; final Double width = profil.getYDim().value; final IShape box =
		 * SpatialCreation.box(scope, height, width, depth); box.setAttribute(IKeyword.NAME,
		 * o.getName().getDecodedValue()); newAxe.transform(box); addAttribtutes(o, box);
		 *
		 * return box; }
		 *
		 * } } } return null;
		 */
	}

	public Double defineDoorDepth(final IScope scope, final IfcObjectPlacement placement,
			final Map<IfcProduct, Double> depths) {
		if (placement instanceof IfcLocalPlacement) {
			final IfcObjectPlacement pla = ((IfcLocalPlacement) placement).getPlacementRelTo();
			if (pla != null && pla.getPlacesObject_Inverse() != null) {
				for (final IfcProduct p : pla.getPlacesObject_Inverse()) {
					if (depths.containsKey(p)) { return depths.get(p); }
				}
			}
			if (pla != null) { return defineDoorDepth(scope, pla, depths); }
		}
		return null;
	}

	public IShape createDoor(final IScope scope, final IfcDoor d, final Map<IfcProduct, Double> depths) {
		if (d.getObjectPlacement() == null) { return null; }
		final Axe newAxe = new Axe();
		final List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, d.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		final double height = d.getOverallHeight().value;
		final double width = d.getOverallWidth().value;
		Double depth = defineDoorDepth(scope, d.getObjectPlacement(), depths);
		if (depth == null || depth == 0.0) {
			depth = width / 10.0;
		}
		IShape box = SpatialCreation.box(scope, width, depth, height);
		box = SpatialTransformations.translated_by(scope, box, GamaPointFactory.create(width / 2.0, 0.0));
		final IList<IShape> pts = GamaListFactory.create(Types.GEOMETRY);
		pts.add(GamaPointFactory.create(-depth / 2.0, 0));
		pts.add(GamaPointFactory.create(depth / 2.0, 0.0));
		final IShape line = SpatialCreation.line(scope, pts);
		box.setAttribute(IKeyword.NAME, d.getName().getDecodedValue());
		box = SpatialTransformations.translated_by(scope, box,
				GamaPointFactory.create(line.getLocation().getX() - line.getPoints().get(0).getX(),
						line.getLocation().getY() - line.getPoints().get(0).getY()));

		addAttribtutes(d, box);
		newAxe.transform(box);

		return box;
	}

	public IShape createWindow(final IScope scope, final IfcWindow d, final Map<IfcProduct, Double> depths) {
		if (d.getObjectPlacement() == null) { return null; }
		final Axe newAxe = new Axe();
		final List<IfcAxis2Placement> aps = new ArrayList<>();
		final List<IfcObjectPlacement> ls = new ArrayList<>();
		IfcObjectPlacement placement = d.getObjectPlacement();
		IfcObjectPlacement placeme = d.getObjectPlacement();
		int index = 0;
		int i = 0;
		while (true) {
			if (placeme instanceof IfcLocalPlacement) {
				final IfcObjectPlacement pla = ((IfcLocalPlacement) placeme).getPlacementRelTo();
				if (pla != null && pla.getPlacesObject_Inverse() != null) {
					for (final IfcProduct p : pla.getPlacesObject_Inverse()) {
						if (p instanceof IfcWall) {
							index = i;
							break;
						}
					}
				}
				if (pla != null) {
					ls.add(pla);
					if (index == 0) {
						placeme = pla;
						i++;
						continue;
					}
				}

			}
			break;
		}

		for (int j = 0; j < index; j++) {
			placement = ls.remove(0);
		}

		relatedTo(scope, placement, aps);
		IfcAxis2Placement axisplFirst = null;
		if (d.getObjectPlacement() instanceof IfcLocalPlacement) {
			axisplFirst = ((IfcLocalPlacement) d.getObjectPlacement()).getRelativePlacement();
		}

		newAxe.update(aps, true);
		final double height = d.getOverallHeight().value;
		final double width = d.getOverallWidth().value;
		Double depth = defineDoorDepth(scope, d.getObjectPlacement(), depths);
		if (depth == null) {
			depth = width / 10.0;
		}

		IShape box = SpatialCreation.box(scope, width, depth, height);

		box.setAttribute(IKeyword.NAME, d.getName().getDecodedValue());
		final IList<IShape> pts = GamaListFactory.create(Types.GEOMETRY);
		pts.add(GamaPointFactory.create(-width / 2.0, 0.0));
		pts.add(GamaPointFactory.create(width / 2.0, 0.0));
		final IShape line = SpatialCreation.line(scope, pts);
		box = SpatialTransformations.translated_by(scope, box,
				GamaPointFactory.create(line.getLocation().getX() - line.getPoints().get(0).getX(), 1.5 * depth));

		addAttribtutes(d, box);
		newAxe.transform(box);

		if (axisplFirst instanceof IfcAxis2Placement2D) {
			final IfcAxis2Placement2D axispl2D = (IfcAxis2Placement2D) axisplFirst;
			if (axispl2D.getRefDirection() != null) {
				final IPoint dir = toPoint(axispl2D.getRefDirection());
				box = SpatialTransformations.rotated_by(scope, box, 90 * dir.getY());
			}
		} else if (axisplFirst instanceof IfcAxis2Placement3D) {
			final IfcAxis2Placement3D axispl3D = (IfcAxis2Placement3D) axisplFirst;
			if (axispl3D.getRefDirection() != null) {
				final IPoint dir = toPoint(axispl3D.getRefDirection());
				box = SpatialTransformations.rotated_by(scope, box, 90 * dir.getY());
			}
		}

		return box;
	}

	public IShape createWall(final IScope scope, final IfcWall w, final Map<IfcProduct, Double> depths) {
		// String name = w.getName().toString();
		if (w.getObjectPlacement() == null) { return null; }

		final Axe newAxe = new Axe();
		final List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, w.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		final IList<IShape> linePts = GamaListFactory.create(Types.POINT);

		for (final IfcRepresentation r : w.getRepresentation().getRepresentations()) {
			for (final IfcRepresentationItem item : r.getItems()) {
				if (!(item instanceof IfcPolyline)) {
					continue;
				}
				final IfcPolyline lineItem = (IfcPolyline) item;
				for (final IfcCartesianPoint pt : lineItem.getPoints()) {
					linePts.add(toPoint(pt));
				}
			}
		}
		final IShape line = SpatialCreation.line(scope, linePts);
		newAxe.transform(line);
		for (final IfcRepresentation r : w.getRepresentation().getRepresentations()) {
			for (final IfcRepresentationItem it : r.getItems()) {
				IfcRepresentationItem item = it;
				while (item instanceof IfcBooleanClippingResult) {
					final IfcBooleanClippingResult bdr = (IfcBooleanClippingResult) item;
					final IfcBooleanOperand op = bdr.getFirstOperand();
					item = (IfcRepresentationItem) op;
				}
				if (!(item instanceof IfcExtrudedAreaSolid)) {
					continue;
				}
				final IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
				final IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
				final Double width = profil.getXDim().value;
				final Double height = profil.getYDim().value;
				final Double depth = solid.getDepth().value;
				depths.put(w, height);

				IShape box = SpatialCreation.box(scope, width, height, depth);
				newAxe.transform(box);

				box = SpatialTransformations.translated_by(scope, box,
						GamaPointFactory.create(line.getLocation().getX() - line.getPoints().get(0).getX(),
								line.getLocation().getY() - line.getPoints().get(0).getY()));
				box.setAttribute(IKeyword.NAME, w.getName().getDecodedValue());
				addAttribtutes(w, box);
				getMaterial(w, box);
				return box;
			}
		}
		return null;
	}

	private void getMaterial(final IfcProduct p, final IShape shape) {
		final IMap<String, Double> materials = GamaMapFactory.create(Types.STRING, Types.FLOAT);
		for (final IfcRelAssociates ra : p.getHasAssociations_Inverse()) {
			if (ra instanceof IfcRelAssociatesMaterial) {
				final IfcRelAssociatesMaterial ram = (IfcRelAssociatesMaterial) ra;
				final IfcMaterialSelect ms = ram.getRelatingMaterial();
				if (ms != null) {
					if (ms instanceof IfcMaterialLayerSetUsage) {
						final IfcMaterialLayerSetUsage mlsu = (IfcMaterialLayerSetUsage) ms;
						final IfcMaterialLayerSet ls = mlsu.getForLayerSet();
						for (final IfcMaterialLayer mls : ls.getMaterialLayers()) {
							final IfcMaterial mat = mls.getMaterial();
							final String name = mat.getName().getDecodedValue();
							materials.put(name, mls.getLayerThickness().value);
						}
					} else if (ms instanceof IfcMaterialLayerSet) {
						final IfcMaterialLayerSet ls = (IfcMaterialLayerSet) ms;
						for (final IfcMaterialLayer mls : ls.getMaterialLayers()) {
							final IfcMaterial mat = mls.getMaterial();
							final String name = mat.getName().getDecodedValue();
							materials.put(name, mls.getLayerThickness().value);
						}
					}
				}
			}
		}

		shape.setAttribute("materials", materials);
	}

	public IShape createSlab(final IScope scope, final IfcSlab s) {
		if (s.getObjectPlacement() == null) { return null; }
		final Axe newAxe = new Axe();
		final List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, s.getObjectPlacement(), aps);
		newAxe.update(aps, true);
		for (final IfcRepresentation rep : s.getRepresentation().getRepresentations()) {
			for (final IfcRepresentationItem item : rep.getItems()) {

				if (item instanceof IfcExtrudedAreaSolid) {
					final IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
					final Double depth = solid.getDepth().value;
					if (solid.getPosition() != null) {
						newAxe.update(solid.getPosition());
					}
					if (solid.getSweptArea() instanceof IfcRectangleProfileDef) {
						final IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
						final Double width = profil.getXDim().value;
						final Double height = profil.getYDim().value;
						IShape box = SpatialCreation.box(scope, width, height, depth);
						box.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
						newAxe.transform(box);
						addAttribtutes(s, box);

						getMaterial(s, box);
						box = SpatialTransformations.translated_by(scope, box, GamaPointFactory.create(0, 0, -depth));
						return box;
					} else if (solid.getSweptArea() instanceof IfcArbitraryClosedProfileDef) {
						final IfcArbitraryClosedProfileDef profil = (IfcArbitraryClosedProfileDef) solid.getSweptArea();
						final IfcCurve curve = profil.getOuterCurve();
						if (curve instanceof IfcPolyline) {
							final IShape shape = toGeom(scope, ((IfcPolyline) curve).getPoints(), true);
							shape.setDepth(depth);
							shape.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
							addAttribtutes(s, shape);
							newAxe.transform(shape);

							getMaterial(s, shape);
							return shape;
						}
						return null;
					}
				}
			}
		}
		return null;
	}

	public IShape createSpace(final IScope scope, final IfcSpace s) {
		if (s.getObjectPlacement() == null) { return null; }
		final Axe newAxe = new Axe();
		final List<IfcAxis2Placement> aps = new ArrayList<>();
		relatedTo(scope, s.getObjectPlacement(), aps);
		newAxe.update(aps, true);

		for (final IfcRepresentation rep : s.getRepresentation().getRepresentations()) {
			for (final IfcRepresentationItem item : rep.getItems()) {
				if (item instanceof IfcExtrudedAreaSolid) {
					final IfcExtrudedAreaSolid solid = (IfcExtrudedAreaSolid) item;
					if (solid.getPosition() != null) {
						newAxe.update(solid.getPosition());
					}
					final Double depth = solid.getDepth().value;
					if (solid.getSweptArea() instanceof IfcRectangleProfileDef) {
						final IfcRectangleProfileDef profil = (IfcRectangleProfileDef) solid.getSweptArea();
						final Double width = profil.getXDim().value;
						final Double height = profil.getYDim().value;
						final IShape box = SpatialCreation.box(scope, width, height, depth);
						box.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
						newAxe.transform(box);
						addAttribtutes(s, box);
						return box; 
					} else if (solid.getSweptArea() instanceof IfcArbitraryClosedProfileDef) {
						final IfcArbitraryClosedProfileDef profil = (IfcArbitraryClosedProfileDef) solid.getSweptArea();
						final IfcCurve curve = profil.getOuterCurve();
						if (curve instanceof IfcPolyline) {
							final IShape shape = toGeom(scope, ((IfcPolyline) curve).getPoints(), true);
							shape.setDepth(depth);
							shape.setAttribute(IKeyword.NAME, s.getName().getDecodedValue());
							newAxe.transform(shape);

							addAttribtutes(s, shape);
							return shape;
						}
						return null;
					}
				}
			}
		}
		return null;
	}

	public void managePropertySet(final IfcPropertySet ps, final IShape shape) {
		for (final IfcProperty p : ps.getHasProperties()) {
			if (p instanceof IfcPropertySingleValue) {
				shape.setAttribute(p.getName().getDecodedValue(),
						((IfcPropertySingleValue) p).getNominalValue().toString());

			}
		}
	}

	public void addAttribtutes(final IfcProduct product, final IShape shape) {
		shape.setAttribute("type", product.getClass().getSimpleName());
		if (product.getIsDefinedBy_Inverse() == null) { return; }
		for (final IfcRelDefines rd : product.getIsDefinedBy_Inverse()) {
			if (rd instanceof IfcRelDefinesByProperties) {
				final IfcRelDefinesByProperties rlp = (IfcRelDefinesByProperties) rd;
				if (rlp.getRelatingPropertyDefinition() instanceof IfcPropertySet) {
					managePropertySet((IfcPropertySet) rlp.getRelatingPropertyDefinition(), shape);
				}
			} else if (rd instanceof IfcRelDefinesByType) {
				final IfcRelDefinesByType rls = (IfcRelDefinesByType) rd;
				if (rls.getRelatingType() == null) {
					continue;
				}
				final IfcTypeObject type = rls.getRelatingType();
				if (type.getHasPropertySets() == null) {
					continue;
				}
				for (final IfcPropertySetDefinition def : type.getHasPropertySets()) {
					if (def instanceof IfcPropertySet) {
						managePropertySet((IfcPropertySet) def, shape);
					}
				}

			}
		}
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		final IList<IShape> geoms = GamaListFactory.create(Types.GEOMETRY);

		try {
			final IfcModel ifcModel = new IfcModel();
			ifcModel.readStepFile(getFile(scope));
		} catch (final Exception e) {
			final ifc2x3javatoolbox.ifcmodel.IfcModel ifcModel = new ifc2x3javatoolbox.ifcmodel.IfcModel();
			try {
				ifcModel.readStepFile(getFile(scope));
			} catch (final Exception e1) {
				e1.printStackTrace();
			}

			final Map<IfcProduct, Double> depths = new Hashtable<>();
			final Collection<IfcWall> walls = ifcModel.getCollection(IfcWall.class);
			for (final IfcWall w : walls) {
				final IShape g = createWall(scope, w, depths);
				if (g != null) {
					geoms.add(g);
				}
			}

			final Collection<IfcSlab> slabs = ifcModel.getCollection(IfcSlab.class);
			final Map<String, IShape> slabsMap = new HashMap<>();
			for (final IfcSlab s : slabs) {
				final IShape g = createSlab(scope, s);
				if (g != null) {
					geoms.add(g);
					slabsMap.put(s.toString(), g);
				}
			}

			final Collection<IfcRoof> roofs = ifcModel.getCollection(IfcRoof.class);
			for (final IfcRoof r : roofs) {
				if (r.getIsDecomposedBy_Inverse() != null) {
					if (r.getIsDefinedBy_Inverse() == null) {
						continue;
					}
					final Map<String, Object> attributes = new Hashtable<>();
					attributes.put("roof_id", r.getTag().getDecodedValue());
					attributes.put("roof_name", r.getName().getDecodedValue());
					for (final IfcRelDefines rd : r.getIsDefinedBy_Inverse()) {
						if (rd instanceof IfcRelDefinesByProperties) {
							final IfcRelDefinesByProperties rlp = (IfcRelDefinesByProperties) rd;
							if (rlp.getRelatingPropertyDefinition() instanceof IfcPropertySet) {
								final IfcPropertySet ps = (IfcPropertySet) rlp.getRelatingPropertyDefinition();
								for (final IfcProperty p : ps.getHasProperties()) {
									if (p instanceof IfcPropertySingleValue) {
										attributes.put(p.getName().getDecodedValue(),
												((IfcPropertySingleValue) p).getNominalValue().toString());

									}
								}
							}
						}
					}
					for (final IfcRelDecomposes dec : r.getIsDecomposedBy_Inverse()) {
						for (final IfcObjectDefinition obj : dec.getRelatedObjects()) {

							final String name = obj.toString();
							final IShape shape = slabsMap.get(name);
							if (shape != null) {
								for (final String key : attributes.keySet()) {
									shape.setAttribute(key, attributes.get(key));
								}
							}
						}
					}
				}
			}

			final Collection<IfcSpace> spaces = ifcModel.getCollection(IfcSpace.class);
			for (final IfcSpace s : spaces) {
				final IShape g = createSpace(scope, s);
				if (g != null) {
					geoms.add(g);
				}
			}

			final Collection<IfcOpeningElement> opening = ifcModel.getCollection(IfcOpeningElement.class);
			for (final IfcOpeningElement o : opening) {
				final IShape g = createOpening(scope, o);
				if (g != null) {
					geoms.add(g);
				}
			}

			final Collection<IfcDoor> doors = ifcModel.getCollection(IfcDoor.class);
			for (final IfcDoor s : doors) {
				final IShape g = createDoor(scope, s, depths);
				if (g != null) {
					geoms.add(g);
				}
			}
			final Collection<IfcWindow> windows = ifcModel.getCollection(IfcWindow.class);
			for (final IfcWindow s : windows) {
				final IShape g = createWindow(scope, s, depths);
				if (g != null) {
					geoms.add(g);
				}
			}
			setBuffer(geoms);
		}

	}

	public void relatedTo(final IScope scope, final IfcObjectPlacement placement, final List<IfcAxis2Placement> aps) {
		if (placement instanceof IfcLocalPlacement) {
			final IfcObjectPlacement pla = ((IfcLocalPlacement) placement).getPlacementRelTo();
			final IfcAxis2Placement axispl = ((IfcLocalPlacement) placement).getRelativePlacement();
			aps.add(axispl);
			if (pla != null) {
				relatedTo(scope, pla, aps);
			}
		}
	}

	@Override
	public IEnvelope computeEnvelope(final IScope scope) {
		boolean didFillBuffer = false;
		if (getBuffer() == null) { 
			fillBuffer(scope);
			didFillBuffer = true;
		}
		if (getBuffer() == null) { return null; }
		IEnvelope env = SpatialCreation.envelope(scope, getBuffer()).getEnvelope();
		if (didFillBuffer) {
			final IPoint vect = GamaPointFactory.create(-env.getMinX(), -env.getMinY(), -env.getMinZ());
			final IList<IShape> newBuffer = GamaListFactory.create(Types.GEOMETRY);
			for (final IShape buff : getBuffer()) { 
				newBuffer.add(SpatialTransformations.translated_by(scope, buff, vect));
			}
			setBuffer(newBuffer);
			env = env.translate(-env.getMinX(), -env.getMinY(), -env.getMinZ());// GeometryUtils.computeEnvelopeFrom(scope,
																				// getBuffer());
		}
		return env;
	}
}
