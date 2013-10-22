package com.sharegogo.wireless.dlna;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.SortCriterion;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.sharegogo.wireless.R;
import com.sharegogo.wireless.SharegogoWirelessApp;
import com.sharegogo.wireless.dlna.ContentDirectoryBrowseActionCallback.BrowseListener;

public class UpnpClient implements RegistryListener, ServiceConnection {

	public static String LOCAL_UID = "LOCAL_UID";

	private static UpnpClient mInstance;
	private List<UpnpClientListener> listeners = new ArrayList<UpnpClientListener>();
	private Set<Device> knownDevices = new HashSet<Device>();
	private AndroidUpnpService androidUpnpService;
	private Context mContext;
	private LinkedList<String> mVisitedObjectIds;
	protected SharedPreferences mPreferences;

	public static UpnpClient getInstance() {
		if (mInstance == null) {
			synchronized (UpnpClient.class) {
				if (mInstance == null) {
					mInstance = new UpnpClient();
				}
			}
		}

		return mInstance;
	}

	private UpnpClient() {
		initialize(SharegogoWirelessApp.getApplication());
	}

	/**
	 * Initialize the Object.
	 * 
	 * @param context
	 *            the context
	 * @return true if initialization completes correctly
	 */
	private boolean initialize(Context context) {
		mContext = context;
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mVisitedObjectIds = new LinkedList<String>();

		// FIXME check if this is right: Context.BIND_AUTO_CREATE kills the
		// service after closing the activity
		return context.bindService(new Intent(context,
				UpnpRegistryService.class), this, Context.BIND_AUTO_CREATE);

	}

	private void deviceAdded(@SuppressWarnings("rawtypes") final Device device) {
		fireDeviceAdded(device);
	}

	private void deviceRemoved(@SuppressWarnings("rawtypes") final Device device) {
		fireDeviceRemoved(device);
	}

	private void deviceUpdated(@SuppressWarnings("rawtypes") final Device device) {
		fireDeviceUpdated(device);
	}

	private void fireDeviceAdded(Device<?, ?, ?> device) {
		for (UpnpClientListener listener : listeners) {
			listener.deviceAdded(device);
		}
	}

	private void fireDeviceRemoved(Device<?, ?, ?> device) {
		for (UpnpClientListener listener : listeners) {
			listener.deviceRemoved(device);
		}
	}

	private void fireDeviceUpdated(Device<?, ?, ?> device) {
		for (UpnpClientListener listener : listeners) {
			listener.deviceUpdated(device);
		}
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {

		setAndroidUpnpService(((AndroidUpnpService) service));
		refreshUpnpDeviceCatalog();

	}

	@Override
	public void onServiceDisconnected(ComponentName className) {
		setAndroidUpnpService(null);

	}

	@Override
	public void remoteDeviceDiscoveryStarted(Registry registry,
			RemoteDevice remotedevice) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.teleal.cling.registry.RegistryListener#remoteDeviceDiscoveryFailed
	 * (org.teleal.cling.registry.Registry,
	 * org.teleal.cling.model.meta.RemoteDevice, java.lang.Exception)
	 */
	@Override
	public void remoteDeviceDiscoveryFailed(Registry registry,
			RemoteDevice remotedevice, Exception exception) {
		Log.d(getClass().getName(), "remoteDeviceDiscoveryFailed: "
				+ remotedevice.getDisplayString(), exception);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.teleal.cling.registry.RegistryListener#remoteDeviceAdded(org.teleal
	 * .cling.registry.Registry, org.teleal.cling.model.meta.RemoteDevice)
	 */
	@Override
	public void remoteDeviceAdded(Registry registry, RemoteDevice remotedevice) {
		Log.d(getClass().getName(),
				"remoteDeviceAdded: " + remotedevice.getDisplayString());
		deviceAdded(remotedevice);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.teleal.cling.registry.RegistryListener#remoteDeviceUpdated(org.teleal
	 * .cling.registry.Registry, org.teleal.cling.model.meta.RemoteDevice)
	 */
	@Override
	public void remoteDeviceUpdated(Registry registry, RemoteDevice remotedevice) {
		Log.d(getClass().getName(),
				"remoteDeviceUpdated: " + remotedevice.getDisplayString());
		deviceUpdated(remotedevice);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.teleal.cling.registry.RegistryListener#remoteDeviceRemoved(org.teleal
	 * .cling.registry.Registry, org.teleal.cling.model.meta.RemoteDevice)
	 */
	@Override
	public void remoteDeviceRemoved(Registry registry, RemoteDevice remotedevice) {
		Log.d(getClass().getName(),
				"remoteDeviceRemoved: " + remotedevice.getDisplayString());
		deviceRemoved(remotedevice);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.teleal.cling.registry.RegistryListener#localDeviceAdded(org.teleal
	 * .cling.registry.Registry, org.teleal.cling.model.meta.LocalDevice)
	 */
	@Override
	public void localDeviceAdded(Registry registry, LocalDevice localdevice) {
		Log.d(getClass().getName(),
				"localDeviceAdded: " + localdevice.getDisplayString());
		this.getRegistry().addDevice(localdevice);
		this.deviceAdded(localdevice);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.teleal.cling.registry.RegistryListener#localDeviceRemoved(org.teleal
	 * .cling.registry.Registry, org.teleal.cling.model.meta.LocalDevice)
	 */
	@Override
	public void localDeviceRemoved(Registry registry, LocalDevice localdevice) {
		Log.d(getClass().getName(),
				"localDeviceRemoved: " + localdevice.getDisplayString());
		this.getRegistry().removeDevice(localdevice);
		this.deviceRemoved(localdevice);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.teleal.cling.registry.RegistryListener#beforeShutdown(org.teleal.
	 * cling.registry.Registry)
	 */
	@Override
	public void beforeShutdown(Registry registry) {
		Log.d(getClass().getName(), "beforeShutdown: " + registry);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.teleal.cling.registry.RegistryListener#afterShutdown()
	 */
	@Override
	public void afterShutdown() {
		Log.d(getClass().getName(), "afterShutdown ");
	}

	// ****************************************************

	/**
	 * Returns a Service of type AVTransport
	 * 
	 * @param device
	 *            the device which provides the service
	 * @return the service of null
	 */
	public Service getAVTransportService(Device<?, ?, ?> device) {
		if (device == null) {
			Log.d(getClass().getName(), "Device is null!");
			return null;
		}
		ServiceId serviceId = new UDAServiceId("AVTransport");
		Service service = device.findService(serviceId);
		if (service != null) {
			Log.d(getClass().getName(),
					"Service found: " + service.getServiceId() + " Type: "
							+ service.getServiceType());
		}
		return service;
	}

	/**
	 * Start an intent with Action.View;
	 * 
	 * @param mime
	 *            the Mimetype to start
	 * @param uris
	 *            the uri to start
	 * @param backround
	 *            starts a background activity
	 */
	protected void intentView(String mime, Uri... uris) {
		if (uris == null || uris.length == 0)
			return;
		Intent intent = null;
		if (mime != null) {
			// test if special activity to choose
			if (mime.indexOf("audio") > -1) {
				boolean background = mPreferences.getBoolean(
						mContext.getString(R.string.settings_audio_app), true);
				if (background) {
					Log.d(getClass().getName(),
							"Starting Background service... ");
					// Intent svc = new Intent(context,
					// BackgroundMusicService.class);
					// if (uris.length == 1) {
					// svc.setData(uris[0]);
					// } else {
					// svc.putExtra(BackgroundMusicService.URIS, uris);
					// }
					// context.startService(svc);
					return;
				} else {
					intent = new Intent(Intent.ACTION_VIEW);
					if (uris.length == 1) {
						intent.setDataAndType(uris[0], mime);
					} else {
						// FIXME How to handle this...
						throw new IllegalStateException("Not yet implemented");
					}
				}
			} else if (mime.indexOf("image") > -1) {
				boolean yaaccImageViewer = mPreferences.getBoolean(
						mContext.getString(R.string.settings_image_app), true);
				if (yaaccImageViewer) {
					// intent = new Intent(context, ImageViewerActivity.class);
					// if (uris.length == 1) {
					// intent.setDataAndType(uris[0], mime);
					// } else {
					// intent.putExtra(ImageViewerActivity.URIS, uris);
					// }
				} else {
					intent = new Intent(Intent.ACTION_VIEW);
					if (uris.length == 1) {
						intent.setDataAndType(uris[0], mime);
					} else {
						// FIXME How to handle this...
						throw new IllegalStateException("Not yet implemented");
					}
				}
			}
		}
		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				mContext.startActivity(intent);
			} catch (ActivityNotFoundException anfe) {
				Resources res = getContext().getResources();
				String text = String.format(
						res.getString(R.string.error_no_activity_found), mime);
				Toast toast = Toast.makeText(getContext(), text,
						Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}

	/**
	 * Add an listener.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	public void addUpnpClientListener(UpnpClientListener listener) {
		listeners.add(listener);
		refreshUpnpDeviceCatalog();
	}

	/**
	 * Remove the given listener.
	 * 
	 * @param listener
	 *            the listener which is to be removed
	 */
	public void removeUpnpClientListener(UpnpClientListener listener) {
		listeners.remove(listener);
	}

	/**
	 * returns the AndroidUpnpService
	 * 
	 * @return the service
	 */
	protected AndroidUpnpService getAndroidUpnpService() {
		return androidUpnpService;
	}

	/**
	 * Returns all registered UpnpDevices.
	 * 
	 * @return the upnpDevices
	 */
	public Collection<Device> getDevices() {
		if (isInitialized()) {
			return getRegistry().getDevices();
		}
		return new ArrayList<Device>();
	}

	/**
	 * Returns all registered UpnpDevices with a ContentDirectory Service.
	 * 
	 * @return the upnpDevices
	 */
	public Collection<Device> getDevicesProvidingContentDirectoryService() {
		if (isInitialized()) {
			return getRegistry().getDevices(
					new UDAServiceType("ContentDirectory"));

		}
		return new ArrayList<Device>();
	}

	/**
	 * Returns all registered UpnpDevices with an AVTransport Service.
	 * 
	 * @return the upnpDevices
	 */
	public Collection<Device> getDevicesProvidingAvTransportService() {
		if (isInitialized()) {
			return getRegistry().getDevices(new UDAServiceType("AVTransport"));

		}
		return new ArrayList<Device>();
	}

	/**
	 * Returns a registered UpnpDevice.
	 * 
	 * @return the upnpDevice null if not found
	 */
	public Device<?, ?, ?> getDevice(String identifier) {
		if (isInitialized()) {
			return getRegistry().getDevice(new UDN(identifier), true);
		}
		return null;
	}

	/**
	 * Returns the cling UpnpService.
	 * 
	 * @return the cling UpnpService
	 */
	public UpnpService getUpnpService() {
		if (!isInitialized()) {
			return null;
		}
		return androidUpnpService.get();
	}

	/**
	 * True if the client is initialized.
	 * 
	 * @return true or false
	 */
	public boolean isInitialized() {
		return getAndroidUpnpService() != null;
	}

	/**
	 * returns the upnp service configuration
	 * 
	 * @return the configuration
	 */
	public UpnpServiceConfiguration getConfiguration() {
		if (!isInitialized()) {
			return null;
		}
		return androidUpnpService.getConfiguration();
	}

	/**
	 * returns the upnp control point
	 * 
	 * @return the control point
	 */
	public ControlPoint getControlPoint() {
		if (!isInitialized()) {
			return null;
		}
		return androidUpnpService.getControlPoint();
	}

	/**
	 * Returns the upnp registry
	 * 
	 * @return the registry
	 */
	public Registry getRegistry() {
		if (!isInitialized()) {
			return null;
		}
		return androidUpnpService.getRegistry();
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * Setting an new upnpRegistryService. If the service is not null, refresh
	 * the device list.
	 * 
	 * @param upnpService
	 */
	protected void setAndroidUpnpService(AndroidUpnpService upnpService) {
		this.androidUpnpService = upnpService;

	}

	/**
	 * refresh the device catalog
	 */
	private void refreshUpnpDeviceCatalog() {
		if (isInitialized()) {
			for (Device<?, ?, ?> device : getAndroidUpnpService().getRegistry()
					.getDevices()) {
				// FIXME: What about removed devices?
				this.deviceAdded(device);
			}

			// Getting ready for future device advertisements
			getAndroidUpnpService().getRegistry().addListener(this);

			searchDevices();
		}
	}

	/**
	 * Browse ContenDirctory asynchronous
	 * 
	 * @param device
	 *            the device to be browsed
	 * @param objectID
	 *            the browsing root
	 * @return the browsing result
	 */
	public void browseAsync(Device<?, ?, ?> device, String objectID,BrowseListener listener) {
		browseAsync(device, objectID, BrowseFlag.DIRECT_CHILDREN, "*", 0L,
				null,listener,new SortCriterion[0]);
	}

	/**
	 * Browse ContenDirctory asynchronous
	 * 
	 * @param device
	 *            the device to be browsed
	 * @param objectID
	 *            the browsing root
	 * @param flag
	 *            kind of browsing @see {@link BrowseFlag}
	 * @param filter
	 *            a filter
	 * @param firstResult
	 *            first result
	 * @param maxResults
	 *            max result count
	 * @param orderBy
	 *            sorting criteria @see {@link SortCriterion}
	 * @return the browsing result
	 */
	public void browseAsync(Device<?, ?, ?> device, String objectID,
			BrowseFlag flag, String filter, long firstResult, Long maxResults,
			BrowseListener listener,SortCriterion... orderBy) {
		Service service = device.findService(new UDAServiceId(
				"ContentDirectory"));
		ContentDirectoryBrowseActionCallback actionCallback = null;
		if (service != null) {
			Log.d(getClass().getName(),
					"#####Service found: " + service.getServiceId() + " Type: "
							+ service.getServiceType());
			actionCallback = new ContentDirectoryBrowseActionCallback(service,
					objectID, flag, filter, firstResult, maxResults, orderBy,listener);
			getControlPoint().execute(actionCallback);
		}
	}

	/**
	 * Search asynchronously for all devices.
	 */
	public void searchDevices() {
		if (isInitialized()) {
			getAndroidUpnpService().getControlPoint().search();
		}
	}

	/**
	 * Returns a player instance initialized with the given didl object
	 * 
	 * @param didlObject
	 *            the object which describes the content to be played
	 * @return the player
	 */
	// public Player initializePlayer(DIDLObject didlObject) {
	// List<PlayableItem> playableItems =
	// toPlayableItems(toItemList(didlObject));
	// return PlayerFactory.createPlayer(this, playableItems);
	// }

	/**
	 * Returns a player instance initialized with the given transport object
	 * 
	 * @param didlObject
	 *            the object which describes the content to be played
	 * @return the player
	 */
	// public Player initializePlayer(AVTransport transport) {
	// PlayableItem playableItem = new PlayableItem();
	// List<PlayableItem>items = new ArrayList<PlayableItem>();
	// if (transport == null)
	// return PlayerFactory.createPlayer(this, items);
	// Log.d(getClass().getName(), "TransportId: " + transport.getInstanceId());
	// PositionInfo positionInfo = transport.getPositionInfo();
	// if (positionInfo == null)
	// return PlayerFactory.createPlayer(this, items);
	//
	// playableItem.setTitle(positionInfo.getTrackMetaData());
	// playableItem.setUri(Uri.parse(positionInfo.getTrackURI()));
	// String fileExtension =
	// MimeTypeMap.getFileExtensionFromUrl(positionInfo.getTrackURI());
	// playableItem.setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension));
	// items.add(playableItem);
	// Log.d(getClass().getName(),
	// "TransportUri: " + positionInfo.getTrackURI());
	// Log.d(getClass().getName(),
	// "Current duration: " + positionInfo.getTrackDuration());
	// Log.d(getClass().getName(),
	// "TrackMetaData: " + positionInfo.getTrackMetaData());
	// Log.d(getClass().getName(),
	// "MimeType: " + playableItem.getMimeType());
	// return PlayerFactory.createPlayer(this, items);
	// }
	//
	/**
	 * Returns the current player instance for the given transport object
	 * 
	 * @param didlObject
	 *            the object which describes the content to be played
	 * @return the player
	 */
	// public Player getCurrentPlayer(AVTransport transport) {
	// PlayableItem playableItem = new PlayableItem();
	// List<PlayableItem>items = new ArrayList<PlayableItem>();
	// if (transport == null)
	// return PlayerFactory.createPlayer(this, items);
	// Log.d(getClass().getName(), "TransportId: " + transport.getInstanceId());
	// PositionInfo positionInfo = transport.getPositionInfo();
	// if (positionInfo == null)
	// return PlayerFactory.createPlayer(this, items);
	//
	// playableItem.setTitle(positionInfo.getTrackMetaData());
	// playableItem.setUri(Uri.parse(positionInfo.getTrackURI()));
	// String fileExtension =
	// MimeTypeMap.getFileExtensionFromUrl(positionInfo.getTrackURI());
	// String mimeType=
	// MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
	// Log.d(getClass().getName(),
	// "MimeType: " + playableItem.getMimeType());
	// List<Player> avTransportPlayers =
	// PlayerFactory.getCurrentPlayersOfType(PlayerFactory.getPlayerClassForMimeType(mimeType));
	// Player result=null;
	// if(avTransportPlayers != null && avTransportPlayers.size() > 0){
	// result = avTransportPlayers.get(0);
	// }
	// return result;
	// }
	//
	/**
	 * Convert cling items into playable items
	 * 
	 * @param items
	 *            the cling items
	 * @return the playable items
	 */
	// private List<PlayableItem> toPlayableItems(List<Item> items){
	// List<PlayableItem> playableItems = new ArrayList<PlayableItem>();
	// //FIXME: filter cover.jpg for testing purpose
	// List<PlayableItem> coverImageItems = new ArrayList<PlayableItem>();
	// int audioItemsCount=0;
	// for (Item item : items) {
	// PlayableItem playableItem = new PlayableItem();
	// playableItem.setTitle(item.getTitle());
	// Res resource = item.getFirstResource();
	// if(resource != null) {
	// playableItem.setUri(Uri.parse(resource.getValue()));
	// playableItem.setMimeType(resource.getProtocolInfo().getContentFormat());
	// //FIXME: filter cover.jpg for testing purpose
	// if(playableItem.getMimeType().startsWith("audio")){
	// audioItemsCount++;
	// }
	// if(playableItem.getMimeType().startsWith("image")){
	// coverImageItems.add(playableItem);
	// }
	// // calculate duration
	// SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
	// long millis = 10000; // 10 sec. default
	// if (resource.getDuration() != null) {
	// try {
	// Date date = dateFormat.parse(resource.getDuration());
	// millis = (date.getHours() * 3600 + date.getMinutes() * 60 + date
	// .getSeconds()) * 1000;
	//
	// } catch (ParseException e) {
	// Log.d(getClass().getName(), "bad duration format", e);
	//
	// }
	// }
	// playableItem.setDuration(millis);
	// }
	// playableItems.add(playableItem);
	// }
	// //FIXME: filter cover.jpg for testing purpose
	// //here comes the magic
	// if(audioItemsCount > 1 && coverImageItems.size() == 1){
	// //hope there is only one cover image
	// playableItems.removeAll(coverImageItems);
	// }
	// return playableItems;
	// }

	
	// public String getLastVisitedObjectId() {
	// if (visitedObjectIds != null && !visitedObjectIds.isEmpty()) {
	// this.visitedObjectIds.removeLast();
	// }
	// if (visitedObjectIds == null || visitedObjectIds.isEmpty()) {
	// return "-1";
	// }
	// return this.visitedObjectIds.pollLast();
	// }

	public void storeNewVisitedObjectId(String newVisitedObjectId) {

		mVisitedObjectIds.addLast(newVisitedObjectId);
	}

	// public String getCurrentObjectId() {
	// return this.visitedObjectIds.peekLast();
	// }

	
	/**
	 * Shutdown the upnp client and all players
	 */
	// public void shutdown() {
	// //shutdown UpnpRegistry
	// boolean result = getContext().stopService(new Intent(context,
	// UpnpRegistryService.class));
	// Log.d(getClass().getName(), "Stopping UpnpRegistryService succsessful= "
	// + result);
	// //shutdown yaacc server service
	// result = getContext().stopService(new Intent(context,
	// YaaccUpnpServerService.class));
	// Log.d(getClass().getName(),
	// "Stopping YaaccUpnpServerService succsessful= " + result);
	// //stop all players
	// PlayerFactory.shutdown();
	// }

}
