const callButton = document.querySelector('#button-call');
callButton.addEventListener('click', () => {
  // https://jitsi.github.io/handbook/docs/dev-guide/dev-guide-iframe
  // const domain = 'meet.jit.si';
  const domain = 'meet.mayfirst.org';
  const options = {
      roomName: '4164126087',
      width: '640px',
      height: '480px',
      parentNode: document.querySelector('#meet'),
      // https://github.com/jitsi/jitsi-meet/blob/master/config.js
      configOverwrite: { 
        enableWelcomePage: false,
        hideConferenceSubject: true,
        hideConferenceTimer: true,
        hideParticipantsStats: true,
        maxFullResolutionParticipants: -1,
        resolution: 640,
        notifications: [],
      },
      // https://github.com/jitsi/jitsi-meet/blob/master/interface_config.js
      // https://github.com/hapi-robo/connect-webapp-old/blob/e8c716d7b0d0f7a043d882d4b3691dfb705f9979/public/js/video.js
      interfaceConfigOverwrite: {
        DEFAULT_BACKGROUND: '#ffffff',
        INITIAL_TOOLBAR_TIMEOUT: 1000,
        TOOLBAR_TIMEOUT: 1000,
        TOOLBAR_ALWAYS_VISIBLE: false,
        SHOW_JITSI_WATERMARK: false,
        SHOW_WATERMARK_FOR_GUESTS: false,
        TOOLBAR_BUTTONS: [
          'microphone', 'desktop', 'fullscreen',
          'fodeviceselection', 'hangup', 'security'
        ],
        DEFAULT_LOCAL_DISPLAY_NAME: 'me',
        DEFAULT_REMOTE_DISPLAY_NAME: 'Remote',
        SETTINGS_SECTIONS: ["devices"],
        CLOSE_PAGE_GUEST_HINT: false,
        SHOW_PROMOTIONAL_CLOSE_PAGE: false,
        RANDOM_AVATAR_URL_PREFIX: false,
        RANDOM_AVATAR_URL_SUFFIX: false,
        ENABLE_FEEDBACK_ANIMATION: false,
        DISABLE_FOCUS_INDICATOR: false,
        DISABLE_DOMINANT_SPEAKER_INDICATOR: false,
        MOBILE_APP_PROMO: false,
        SHOW_CHROME_EXTENSION_BANNER: false
      },

  };

  const api = new JitsiMeetExternalAPI(domain, options);
  api.executeCommand('setVideoQuality', 640);
  api.executeCommand('displayName', 'Me');

  api.addListener('participantJoined', (e) => {
    console.log(`${e.displayName} (${e.id}) joined the room`);
  });

  api.addListener('participantLeft', (e) => {
    console.log(`${e.id} left the room`);
  });

  api.addListener('videoConferenceJoined', (e) => {
    console.log(`${e.displayName} (${e.id}) has joined the room ${e.roomName}`);
  });

  api.addListener('videoConferenceLeft', (e) => {
    console.log(`Local user has left the room ${e.roomName}`);
    document.querySelector('#meet').innerHTML = '';
  });

  api.addListener('readyToClose', (e) => {
    console.log('Ready to close');
  });
});

