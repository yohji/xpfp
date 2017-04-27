/**
 *  Copyright (c) 2017 Marco Merli <yohji@marcomerli.net>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package net.marcomerli.xpfp.fn;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.sis.distance.DistanceUtils;

import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.data.Settings;
import net.marcomerli.xpfp.model.Location;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class GeoFn {

	private static final TSAGeoMag geoMag = new TSAGeoMag();
	private static final GeoApiContext context;
	static {
		context = new GeoApiContext();
		context.setConnectTimeout(5, TimeUnit.SECONDS);
		context.disableRetries();
	}

	public static void init()
	{
		context.setApiKey(Context.getSettings().getProperty(Settings.GEOAPI_KEY));
		context.setProxy(Context.getProxy());
	}

	public static double elevationOf(Location location) throws Exception
	{
		double elev = ElevationApi.getByPoint(context, location)
			.await().elevation;

		location.alt = elev;
		return elev;
	}

	public static double distance(Location from, Location to)
	{
		double dist = DistanceUtils.getHaversineDistance(
			from.lat, from.lng,
			to.lat, to.lng);

		return dist * 1000;
	}

	public static int bearing(Location from, Location to)
	{
		// FIXME: resolve for good the bearing problem
		return (int) (_bearing(from, to) + declination(from));
	}

	private static double _bearing(Location from, Location to)
	{
		double y = Math.sin(to.lng - from.lng) * Math.cos(to.lat);
		double x = Math.cos(from.lat) * Math.sin(to.lat) -
			Math.sin(from.lat) * Math.cos(to.lat) * Math.cos(to.lng - from.lng);

		return ((Math.round(Math.toDegrees(Math.atan2(y, x)) + 360) % 360));
	}

	public static double declination(Location loc)
	{
		return geoMag.getDeclination(loc.lat, loc.lng);
	}

	private GeoFn() {}
}

/**
 * PUBLIC DOMAIN NOTICE
 * This program was prepared by Los Alamos National Security, LLC
 * at Los Alamos National Laboratory (LANL) under contract No.
 * DE-AC52-06NA25396 with the U.S. Department of Energy (DOE).
 * All rights in the program are reserved by the DOE and
 * Los Alamos National Security, LLC. Permission is granted to the
 * public to copy and use this software without charge,
 * provided that this Notice and any statement of authorship are
 * reproduced on all copies. Neither the U.S. Government nor LANS
 * makes any warranty, express or implied, or assumes any liability
 * or responsibility for the use of this software.
 * 
 * 
 * License Statement from the NOAA
 * The WMM source code is in the public domain and not licensed or
 * under copyright. The information and software may be used freely
 * by the public. As required by 17 U.S.C. 403, third parties producing
 * copyrighted works consisting predominantly of the material produced
 * by U.S. government agencies must provide notice with such work(s)
 * identifying the U.S. Government material incorporated and stating
 * that such material is not subject to copyright protection.
 * 
 * 
 * ////////////////////////////////////////////////////////////////////////////
 * //
 * //GeoMag.java - originally geomag.c
 * //Ported to Java 1.0.2 by Tim Walker
 * //tim.walker@worldnet.att.net
 * //tim@acusat.com
 * //
 * //Updated: 1/28/98
 * //
 * //Original source geomag.c available at
 * //http://www.ngdc.noaa.gov/seg/potfld/DoDWMM.html
 * //
 * //NOTE: original comments from the geomag.c source file are in ALL CAPS
 * //Tim's added comments for the port to Java are not
 * //
 * ////////////////////////////////////////////////////////////////////////////
 */
class TSAGeoMag {

	private static Logger logger = Logger.getLogger(TSAGeoMag.class);

	/**
	 * Geodetic altitude in km. An input,
	 * but set to zero in this class. Changed
	 * back to an input in version 5. If not specified,
	 * then is 0.
	 */
	private double alt = 0;

	/**
	 * Geodetic latitude in deg. An input.
	 */
	private double glat = 0;

	/**
	 * Geodetic longitude in deg. An input.
	 */
	private double glon = 0;

	/**
	 * Time in decimal years. An input.
	 */
	private double time = 0;

	/**
	 * Geomagnetic declination in deg.
	 * East is positive, West is negative.
	 * (The negative of variation.)
	 */
	private double dec = 0;

	/**
	 * Geomagnetic inclination in deg.
	 * Down is positive, up is negative.
	 */
	private double dip = 0;
	/**
	 * Geomagnetic total intensity, in nano Teslas.
	 */
	private double ti = 0;

	/**
	 * Geomagnetic grid variation, referenced to
	 * grid North. Not calculated or output in version 5.0.
	 */
	// private double gv = 0;

	/**
	 * The maximum number of degrees of the spherical harmonic model.
	 */
	private int maxdeg = 12;

	/**
	 * The maximum order of spherical harmonic model.
	 */
	private int maxord;

	/**
	 * Added in version 5. In earlier versions the date for the calculation was held as a
	 * constant. Now the default date is set to 2.5 years plus the epoch read from the
	 * input file.
	 */
	private double defaultDate = 2017.5;

	/**
	 * Added in version 5. In earlier versions the altitude for the calculation was held as a
	 * constant at 0. In version 5, if no altitude is specified in the calculation, this
	 * altitude is used by default.
	 */
	private final double defaultAltitude = 0;

	/**
	 * The Gauss coefficients of main geomagnetic model (nt).
	 */
	private double c[][] = new double[13][13];

	/**
	 * The Gauss coefficients of secular geomagnetic model (nt/yr).
	 */
	private double cd[][] = new double[13][13];

	/**
	 * The time adjusted geomagnetic gauss coefficients (nt).
	 */
	private double tc[][] = new double[13][13];

	/**
	 * The theta derivative of p(n,m) (unnormalized).
	 */
	private double dp[][] = new double[13][13];

	/**
	 * The Schmidt normalization factors.
	 */
	private double snorm[] = new double[169];

	/**
	 * The sine of (m*spherical coord. longitude).
	 */
	private double sp[] = new double[13];

	/**
	 * The cosine of (m*spherical coord. longitude).
	 */
	private double cp[] = new double[13];
	private double fn[] = new double[13];
	private double fm[] = new double[13];

	/**
	 * The associated Legendre polynomials for m=1 (unnormalized).
	 */
	private double pp[] = new double[13];

	private double k[][] = new double[13][13];

	/**
	 * The variables otime (old time), oalt (old altitude),
	 * olat (old latitude), olon (old longitude), are used to
	 * store the values used from the previous calculation to
	 * save on calculation time if some inputs don't change.
	 */
	private double otime, oalt, olat, olon;

	/** The date in years, for the start of the valid time of the fit coefficients */
	private double epoch;

	/**
	 * bx is the north south field intensity
	 * by is the east west field intensity
	 * bz is the vertical field intensity positive downward
	 * bh is the horizontal field intensity
	 */
	private double bx, by, bz, bh;
	private double re, a2, b2, c2, a4, b4, c4;
	private double r, d, ca, sa, ct, st; // even though these only occur in one method, they must be
								// created here, or won't have correct values calculated
	// These values are only recalculated if the altitude changes.

	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Instantiates object by calling initModel().
	 */
	public TSAGeoMag() {
		// read model data from file and initialize the GeoMag routine
		initModel();
	}

	/**
	 * Reads data from file and initializes magnetic model. If
	 * the file is not present, or an IO exception occurs, then the internal
	 * values valid for 2015 will be used. Note that the last line of the
	 * WMM.COF file must be 9999... for this method to read in the input
	 * file properly.
	 */
	private void initModel()
	{
		glat = 0;
		glon = 0;
		// bOutDated = false;
		// String strModel = new String();
		// String strFile = new String("WMM.COF");
		// String strFile = new String("wmm-95.dat");

		// INITIALIZE CONSTANTS
		maxord = maxdeg;
		sp[0] = 0.0;
		cp[0] = snorm[0] = pp[0] = 1.0;
		dp[0][0] = 0.0;
		/**
		 * Semi-major axis of WGS-84 ellipsoid, in km.
		 */
		double a = 6378.137;
		/**
		 * Semi-minor axis of WGS-84 ellipsoid, in km.
		 */
		double b = 6356.7523142;
		/**
		 * Mean radius of IAU-66 ellipsoid, in km.
		 */
		re = 6371.2;
		a2 = a * a;
		b2 = b * b;
		c2 = a2 - b2;
		a4 = a2 * a2;
		b4 = b2 * b2;
		c4 = a4 - b4;

		try {
			// open data file and parse values
			// InputStream is;
			Reader is;

			InputStream input = getClass().getClassLoader().getResourceAsStream("WMM.COF");
			if (input == null)
				throw new FileNotFoundException("WMM.COF not found");
			is = new InputStreamReader(input);
			StreamTokenizer str = new StreamTokenizer(is);

			// READ WORLD MAGNETIC MODEL SPHERICAL HARMONIC COEFFICIENTS
			c[0][0] = 0.0;
			cd[0][0] = 0.0;
			str.nextToken();
			epoch = str.nval;
			defaultDate = epoch + 2.5;
			logger.trace("TSAGeoMag Epoch is: " + epoch);
			logger.trace("TSAGeoMag default date is: " + defaultDate);
			str.nextToken();
			// strModel = str.sval;
			str.nextToken();

			// loop to get data from file
			while (true) {
				str.nextToken();
				if (str.nval >= 9999) // end of file
					break;

				int n = (int) str.nval;
				str.nextToken();
				int m = (int) str.nval;
				str.nextToken();
				double gnm = str.nval;
				str.nextToken();
				double hnm = str.nval;
				str.nextToken();
				double dgnm = str.nval;
				str.nextToken();
				double dhnm = str.nval;

				if (m <= n) {
					c[m][n] = gnm;
					cd[m][n] = dgnm;

					if (m != 0) {
						c[n][m - 1] = hnm;
						cd[n][m - 1] = dhnm;
					}
				}

			} // while(true)

			is.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		// CONVERT SCHMIDT NORMALIZED GAUSS COEFFICIENTS TO UNNORMALIZED
		snorm[0] = 1.0;
		for (int n = 1; n <= maxord; n++) {

			snorm[n] = snorm[n - 1] * (2 * n - 1) / n;
			int j = 2;

			for (int m = 0, D1 = 1, D2 = (n - m + D1) / D1; D2 > 0; D2--, m += D1) {
				k[m][n] = (double) (((n - 1) * (n - 1)) - (m * m)) / (double) ((2 * n - 1) * (2 * n - 3));
				if (m > 0) {
					double flnmj = ((n - m + 1) * j) / (double) (n + m);
					snorm[n + m * 13] = snorm[n + (m - 1) * 13] * Math.sqrt(flnmj);
					j = 1;
					c[n][m - 1] = snorm[n + m * 13] * c[n][m - 1];
					cd[n][m - 1] = snorm[n + m * 13] * cd[n][m - 1];
				}
				c[m][n] = snorm[n + m * 13] * c[m][n];
				cd[m][n] = snorm[n + m * 13] * cd[m][n];
			} // for(m...)

			fn[n] = (n + 1);
			fm[n] = n;

		} // for(n...)

		k[1][1] = 0.0;

		otime = oalt = olat = olon = - 1000.0;

	}

	/**
	 * <p>
	 * <b>PURPOSE:</b> THIS ROUTINE COMPUTES THE DECLINATION (DEC),
	 * INCLINATION (DIP), TOTAL INTENSITY (TI) AND
	 * GRID VARIATION (GV - POLAR REGIONS ONLY, REFERENCED
	 * TO GRID NORTH OF POLAR STEREOGRAPHIC PROJECTION) OF
	 * THE EARTH'S MAGNETIC FIELD IN GEODETIC COORDINATES
	 * FROM THE COEFFICIENTS OF THE CURRENT OFFICIAL
	 * DEPARTMENT OF DEFENSE (DOD) SPHERICAL HARMONIC WORLD
	 * MAGNETIC MODEL (WMM-2010). THE WMM SERIES OF MODELS IS
	 * UPDATED EVERY 5 YEARS ON JANUARY 1'ST OF THOSE YEARS
	 * WHICH ARE DIVISIBLE BY 5 (I.E. 1980, 1985, 1990 ETC.)
	 * BY THE NAVAL OCEANOGRAPHIC OFFICE IN COOPERATION
	 * WITH THE BRITISH GEOLOGICAL SURVEY (BGS). THE MODEL
	 * IS BASED ON GEOMAGNETIC SURVEY MEASUREMENTS FROM
	 * AIRCRAFT, SATELLITE AND GEOMAGNETIC OBSERVATORIES.
	 * </p>
	 * <p>
	 *
	 *
	 *
	 * <b>ACCURACY:</b> IN OCEAN AREAS AT THE EARTH'S SURFACE OVER THE
	 * ENTIRE 5 YEAR LIFE OF A DEGREE AND ORDER 12
	 * SPHERICAL HARMONIC MODEL SUCH AS WMM-95, THE ESTIMATED
	 * RMS ERRORS FOR THE VARIOUS MAGENTIC COMPONENTS ARE:
	 * </p>
	 * <ul>
	 * DEC - 0.5 Degrees<br>
	 * DIP - 0.5 Degrees<br>
	 * TI - 280.0 nanoTeslas (nT)<br>
	 * GV - 0.5 Degrees<br>
	 * </ul>
	 *
	 * <p>
	 * OTHER MAGNETIC COMPONENTS THAT CAN BE DERIVED FROM
	 * THESE FOUR BY SIMPLE TRIGONOMETRIC RELATIONS WILL
	 * HAVE THE FOLLOWING APPROXIMATE ERRORS OVER OCEAN AREAS:
	 * </p>
	 * <ul>
	 * X - 140 nT (North)<br>
	 * Y - 140 nT (East)<br>
	 * Z - 200 nT (Vertical) Positive is down<br>
	 * H - 200 nT (Horizontal)<br>
	 * </ul>
	 *
	 * <p>
	 * OVER LAND THE RMS ERRORS ARE EXPECTED TO BE SOMEWHAT
	 * HIGHER, ALTHOUGH THE RMS ERRORS FOR DEC, DIP AND GV
	 * ARE STILL ESTIMATED TO BE LESS THAN 0.5 DEGREE, FOR
	 * THE ENTIRE 5-YEAR LIFE OF THE MODEL AT THE EARTH's
	 * SURFACE. THE OTHER COMPONENT ERRORS OVER LAND ARE
	 * MORE DIFFICULT TO ESTIMATE AND SO ARE NOT GIVEN.
	 * </p>
	 * <p>
	 *
	 * THE ACCURACY AT ANY GIVEN TIME OF ALL FOUR
	 * GEOMAGNETIC PARAMETERS DEPENDS ON THE GEOMAGNETIC
	 * LATITUDE. THE ERRORS ARE LEAST AT THE EQUATOR AND
	 * GREATEST AT THE MAGNETIC POLES.
	 * </p>
	 * <p>
	 *
	 * IT IS VERY IMPORTANT TO NOTE THAT A DEGREE AND
	 * ORDER 12 MODEL, SUCH AS WMM-2010 DESCRIBES ONLY
	 * THE LONG WAVELENGTH SPATIAL MAGNETIC FLUCTUATIONS
	 * DUE TO EARTH'S CORE. NOT INCLUDED IN THE WMM SERIES
	 * MODELS ARE INTERMEDIATE AND SHORT WAVELENGTH
	 * SPATIAL FLUCTUATIONS OF THE GEOMAGNETIC FIELD
	 * WHICH ORIGINATE IN THE EARTH'S MANTLE AND CRUST.
	 * CONSEQUENTLY, ISOLATED ANGULAR ERRORS AT VARIOUS
	 * POSITIONS ON THE SURFACE (PRIMARILY OVER LAND, IN
	 * CONTINENTAL MARGINS AND OVER OCEANIC SEAMOUNTS,
	 * RIDGES AND TRENCHES) OF SEVERAL DEGREES MAY BE
	 * EXPECTED. ALSO NOT INCLUDED IN THE MODEL ARE
	 * NONSECULAR TEMPORAL FLUCTUATIONS OF THE GEOMAGNETIC
	 * FIELD OF MAGNETOSPHERIC AND IONOSPHERIC ORIGIN.
	 * DURING MAGNETIC STORMS, TEMPORAL FLUCTUATIONS CAN
	 * CAUSE SUBSTANTIAL DEVIATIONS OF THE GEOMAGNETIC
	 * FIELD FROM MODEL VALUES. IN ARCTIC AND ANTARCTIC
	 * REGIONS, AS WELL AS IN EQUATORIAL REGIONS, DEVIATIONS
	 * FROM MODEL VALUES ARE BOTH FREQUENT AND PERSISTENT.
	 * </p>
	 * <p>
	 *
	 * IF THE REQUIRED DECLINATION ACCURACY IS MORE
	 * STRINGENT THAN THE WMM SERIES OF MODELS PROVIDE, THEN
	 * THE USER IS ADVISED TO REQUEST SPECIAL (REGIONAL OR
	 * LOCAL) SURVEYS BE PERFORMED AND MODELS PREPARED BY
	 * THE USGS, WHICH OPERATES THE US GEOMAGNETIC
	 * OBSERVATORIES. REQUESTS OF THIS NATURE SHOULD
	 * BE MADE THROUGH NIMA AT THE ADDRESS ABOVE.
	 * </p>
	 * <p>
	 *
	 *
	 *
	 * NOTE: THIS VERSION OF GEOMAG USES THE WMM-2010 GEOMAGNETIC
	 * MODEL REFERENCED TO THE WGS-84 GRAVITY MODEL ELLIPSOID
	 * </p>
	 *
	 * @param fLat The latitude in decimal degrees.
	 * @param fLon The longitude in decimal degrees.
	 * @param year The date as a decimal year.
	 * @param altitude The altitude in kilometers.
	 */
	private void calcGeoMag(double fLat, double fLon, double year, double altitude)
	{

		glat = fLat;
		glon = fLon;
		alt = altitude;
		/**
		 * The date in decimal years for calculating the magnetic field components.
		 */
		time = year;

		double dt = time - epoch;
		// if (otime < 0.0 && (dt < 0.0 || dt > 5.0))
		// if(bCurrent){
		// if (dt < 0.0 || dt > 5.0)
		// bOutDated = true;
		// else
		// bOutDated = false;
		// }

		double pi = Math.PI;
		double dtr = (pi / 180.0);
		double rlon = glon * dtr;
		double rlat = glat * dtr;
		double srlon = Math.sin(rlon);
		double srlat = Math.sin(rlat);
		double crlon = Math.cos(rlon);
		double crlat = Math.cos(rlat);
		double srlat2 = srlat * srlat;
		double crlat2 = crlat * crlat;
		sp[1] = srlon;
		cp[1] = crlon;

		// CONVERT FROM GEODETIC COORDS. TO SPHERICAL COORDS.
		if (alt != oalt || glat != olat) {
			double q = Math.sqrt(a2 - c2 * srlat2);
			double q1 = alt * q;
			double q2 = ((q1 + a2) / (q1 + b2)) * ((q1 + a2) / (q1 + b2));
			ct = srlat / Math.sqrt(q2 * crlat2 + srlat2);
			st = Math.sqrt(1.0 - (ct * ct));
			double r2 = ((alt * alt) + 2.0 * q1 + (a4 - c4 * srlat2) / (q * q));
			r = Math.sqrt(r2);
			d = Math.sqrt(a2 * crlat2 + b2 * srlat2);
			ca = (alt + d) / r;
			sa = c2 * crlat * srlat / (r * d);
		}
		if (glon != olon) {
			for (int m = 2; m <= maxord; m++) {
				sp[m] = sp[1] * cp[m - 1] + cp[1] * sp[m - 1];
				cp[m] = cp[1] * cp[m - 1] - sp[1] * sp[m - 1];
			}
		}
		double aor = re / r;
		double ar = aor * aor;
		double br = 0, bt = 0, bp = 0, bpp = 0;

		for (int n = 1; n <= maxord; n++) {
			ar = ar * aor;
			for (int m = 0, D3 = 1, D4 = (n + m + D3) / D3; D4 > 0; D4--, m += D3) {

				// COMPUTE UNNORMALIZED ASSOCIATED LEGENDRE POLYNOMIALS
				// AND DERIVATIVES VIA RECURSION RELATIONS
				if (alt != oalt || glat != olat) {
					if (n == m) {
						snorm[n + m * 13] = st * snorm[n - 1 + (m - 1) * 13];
						dp[m][n] = st * dp[m - 1][n - 1] + ct * snorm[n - 1 + (m - 1) * 13];
					}
					if (n == 1 && m == 0) {
						snorm[n + m * 13] = ct * snorm[n - 1 + m * 13];
						dp[m][n] = ct * dp[m][n - 1] - st * snorm[n - 1 + m * 13];
					}
					if (n > 1 && n != m) {
						if (m > n - 2)
							snorm[n - 2 + m * 13] = 0.0;
						if (m > n - 2)
							dp[m][n - 2] = 0.0;
						snorm[n + m * 13] = ct * snorm[n - 1 + m * 13] - k[m][n] * snorm[n - 2 + m * 13];
						dp[m][n] = ct * dp[m][n - 1] - st * snorm[n - 1 + m * 13] - k[m][n] * dp[m][n - 2];
					}
				}

				// TIME ADJUST THE GAUSS COEFFICIENTS

				if (time != otime) {
					tc[m][n] = c[m][n] + dt * cd[m][n];

					if (m != 0)
						tc[n][m - 1] = c[n][m - 1] + dt * cd[n][m - 1];
				}

				// ACCUMULATE TERMS OF THE SPHERICAL HARMONIC EXPANSIONS
				double temp1, temp2;
				double par = ar * snorm[n + m * 13];
				if (m == 0) {
					temp1 = tc[m][n] * cp[m];
					temp2 = tc[m][n] * sp[m];
				}
				else {
					temp1 = tc[m][n] * cp[m] + tc[n][m - 1] * sp[m];
					temp2 = tc[m][n] * sp[m] - tc[n][m - 1] * cp[m];
				}

				bt = bt - ar * temp1 * dp[m][n];
				bp += (fm[m] * temp2 * par);
				br += (fn[n] * temp1 * par);

				// SPECIAL CASE: NORTH/SOUTH GEOGRAPHIC POLES

				if (st == 0.0 && m == 1) {
					if (n == 1)
						pp[n] = pp[n - 1];
					else
						pp[n] = ct * pp[n - 1] - k[m][n] * pp[n - 2];
					double parp = ar * pp[n];
					bpp += (fm[m] * temp2 * parp);
				}

			} // for(m...)

		} // for(n...)

		if (st == 0.0)
			bp = bpp;
		else
			bp /= st;

		// ROTATE MAGNETIC VECTOR COMPONENTS FROM SPHERICAL TO
		// GEODETIC COORDINATES
		// bx must be the east-west field component
		// by must be the north-south field component
		// bz must be the vertical field component.
		bx = - bt * ca - br * sa;
		by = bp;
		bz = bt * sa - br * ca;

		// COMPUTE DECLINATION (DEC), INCLINATION (DIP) AND
		// TOTAL INTENSITY (TI)

		bh = Math.sqrt((bx * bx) + (by * by));
		ti = Math.sqrt((bh * bh) + (bz * bz));
		// Calculate the declination.
		dec = (Math.atan2(by, bx) / dtr);
		logger.trace("Dec is: " + dec);
		dip = (Math.atan2(bz, bh) / dtr);

		// This is the variation for grid navigation.
		// Not used at this time. See St. Ledger for explanation.
		// COMPUTE MAGNETIC GRID VARIATION IF THE CURRENT
		// GEODETIC POSITION IS IN THE ARCTIC OR ANTARCTIC
		// (I.E. GLAT > +55 DEGREES OR GLAT < -55 DEGREES)
		// Grid North is referenced to the 0 Meridian of a polar
		// stereographic projection.

		// OTHERWISE, SET MAGNETIC GRID VARIATION TO -999.0
		/*
		 * gv = -999.0;
		 * if (Math.abs(glat) >= 55.){
		 * if (glat > 0.0 && glon >= 0.0)
		 * gv = dec-glon;
		 * if (glat > 0.0 && glon < 0.0)
		 * gv = dec + Math.abs(glon);
		 * if (glat < 0.0 && glon >= 0.0)
		 * gv = dec+glon;
		 * if (glat < 0.0 && glon < 0.0)
		 * gv = dec - Math.abs(glon);
		 * if (gv > +180.0)
		 * gv -= 360.0;
		 * if (gv < -180.0)
		 * gv += 360.0;
		 * }
		 */
		otime = time;
		oalt = alt;
		olat = glat;
		olon = glon;

	}
	/**
	 * Returns the declination from the Department of
	 * Defense geomagnetic model and data, in degrees. The
	 * magnetic heading + declination = true heading. The date and
	 * altitude are the defaults, of half way through the valid
	 * 5 year period, and 0 elevation.
	 * (True heading + variation = magnetic heading.)
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * 
	 * @return The declination in degrees.
	 */
	public double getDeclination(double dlat, double dlong)
	{
		calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
		return dec;
	}
	/**
	 * Returns the declination from the Department of
	 * Defense geomagnetic model and data, in degrees. The
	 * magnetic heading + declination = true heading.
	 * (True heading + variation = magnetic heading.)
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * @param year The date as a decimial year.
	 * @param altitude The altitude in kilometers.
	 * 
	 * @return The declination in degrees.
	 */
	public double getDeclination(double dlat, double dlong, double year, double altitude)
	{
		calcGeoMag(dlat, dlong, year, altitude);
		return dec;
	}
	/**
	 * Returns the magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla. The date and
	 * altitude are the defaults, of half way through the valid
	 * 5 year period, and 0 elevation.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * 
	 * @return Magnetic field strength in nano Tesla.
	 */
	public double getIntensity(double dlat, double dlong)
	{
		calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
		return ti;
	}

	/**
	 * Returns the magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * @param year Date of the calculation in decimal years.
	 * @param altitude Altitude of the calculation in kilometers.
	 * 
	 * @return Magnetic field strength in nano Tesla.
	 */
	public double getIntensity(double dlat, double dlong, double year, double altitude)
	{
		calcGeoMag(dlat, dlong, year, altitude);
		return ti;
	}
	/**
	 * Returns the horizontal magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla. The date and
	 * altitude are the defaults, of half way through the valid
	 * 5 year period, and 0 elevation.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * 
	 * @return The horizontal magnetic field strength in nano Tesla.
	 */
	public double getHorizontalIntensity(double dlat, double dlong)
	{
		calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
		return bh;
	}

	/**
	 * Returns the horizontal magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * @param year Date of the calculation in decimal years.
	 * @param altitude Altitude of the calculation in kilometers.
	 * 
	 * @return The horizontal magnetic field strength in nano Tesla.
	 */
	public double getHorizontalIntensity(double dlat, double dlong, double year, double altitude)
	{
		calcGeoMag(dlat, dlong, year, altitude);
		return bh;
	}
	/**
	 * Returns the vertical magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla. The date and
	 * altitude are the defaults, of half way through the valid
	 * 5 year period, and 0 elevation.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * 
	 * @return The vertical magnetic field strength in nano Tesla.
	 */
	public double getVerticalIntensity(double dlat, double dlong)
	{
		calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
		return bz;
	}

	/**
	 * Returns the vertical magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * @param year Date of the calculation in decimal years.
	 * @param altitude Altitude of the calculation in kilometers.
	 * 
	 * @return The vertical magnetic field strength in nano Tesla.
	 */
	public double getVerticalIntensity(double dlat, double dlong, double year, double altitude)
	{
		calcGeoMag(dlat, dlong, year, altitude);
		return bz;
	}
	/**
	 * Returns the northerly magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla. The date and
	 * altitude are the defaults, of half way through the valid
	 * 5 year period, and 0 elevation.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * 
	 * @return The northerly component of the magnetic field strength in nano Tesla.
	 */
	public double getNorthIntensity(double dlat, double dlong)
	{
		calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
		return bx;
	}

	/**
	 * Returns the northerly magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * @param year Date of the calculation in decimal years.
	 * @param altitude Altitude of the calculation in kilometers.
	 * 
	 * @return The northerly component of the magnetic field strength in nano Tesla.
	 */
	public double getNorthIntensity(double dlat, double dlong, double year, double altitude)
	{
		calcGeoMag(dlat, dlong, year, altitude);
		return bx;
	}
	/**
	 * Returns the easterly magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla. The date and
	 * altitude are the defaults, of half way through the valid
	 * 5 year period, and 0 elevation.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * 
	 * @return The easterly component of the magnetic field strength in nano Tesla.
	 */
	public double getEastIntensity(double dlat, double dlong)
	{
		calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
		return by;
	}

	/**
	 * Returns the easterly magnetic field intensity from the
	 * Department of Defense geomagnetic model and data
	 * in nano Tesla.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * @param year Date of the calculation in decimal years.
	 * @param altitude Altitude of the calculation in kilometers.
	 * 
	 * @return The easterly component of the magnetic field strength in nano Tesla.
	 */
	public double getEastIntensity(double dlat, double dlong, double year, double altitude)
	{
		calcGeoMag(dlat, dlong, year, altitude);
		return by;
	}
	/**
	 * Returns the magnetic field dip angle from the
	 * Department of Defense geomagnetic model and data,
	 * in degrees. The date and
	 * altitude are the defaults, of half way through the valid
	 * 5 year period, and 0 elevation.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * 
	 * @return The magnetic field dip angle, in degrees.
	 */
	public double getDipAngle(double dlat, double dlong)
	{
		calcGeoMag(dlat, dlong, defaultDate, defaultAltitude);
		return dip;
	}

	/**
	 * Returns the magnetic field dip angle from the
	 * Department of Defense geomagnetic model and data,
	 * in degrees.
	 *
	 * @param dlat Latitude in decimal degrees.
	 * @param dlong Longitude in decimal degrees.
	 * @param year Date of the calculation in decimal years.
	 * @param altitude Altitude of the calculation in kilometers.
	 * 
	 * @return The magnetic field dip angle, in degrees.
	 */
	public double getDipAngle(double dlat, double dlong, double year, double altitude)
	{
		calcGeoMag(dlat, dlong, year, altitude);
		return dip;
	}

	/**
	 * <p>
	 * Given a Gregorian Calendar object, this returns the decimal year
	 * value for the calendar, accurate to the day of the input calendar.
	 * The hours, minutes, and seconds of the date are ignored.
	 * </p>
	 * <p>
	 * 
	 * If the input Gregorian Calendar is new GregorianCalendar(2012, 6, 1), all of
	 * the first of July is counted, and this returns 2012.5. (183 days out of 366)
	 * </p>
	 * <p>
	 * 
	 * If the input Gregorian Calendar is new GregorianCalendar(2010, 0, 0), the first
	 * of January is not counted, and this returns 2010.0
	 * </p>
	 * <p>
	 * 
	 * @param cal Has the date (year, month, and day of the month)
	 * @return The date in decimal years
	 */
	public double decimalYear(GregorianCalendar cal)
	{
		int year = cal.get(Calendar.YEAR);
		double daysInYear;
		if (cal.isLeapYear(year)) {
			daysInYear = 366.0;
		}
		else {
			daysInYear = 365.0;
		}

		return year + (cal.get(Calendar.DAY_OF_YEAR)) / daysInYear;
	}
}
